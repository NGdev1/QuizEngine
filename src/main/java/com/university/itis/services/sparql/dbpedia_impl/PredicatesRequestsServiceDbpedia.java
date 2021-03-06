package com.university.itis.services.sparql.dbpedia_impl;

import com.university.itis.dto.semantic.EntityDto;
import com.university.itis.dto.semantic.TripleDto;
import com.university.itis.services.sparql.PredicatesRequestsService;
import com.university.itis.utils.PrefixesStorage;
import com.university.itis.utils.SparqlHttpClient;
import com.university.itis.utils.UriStorage;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PredicatesRequestsServiceDbpedia implements PredicatesRequestsService {
    private final PrefixesStorage prefixesStorage;
    private final SparqlHttpClient sparqlHttpClient;
    private final UriStorage uriStorage;

    public PredicatesRequestsServiceDbpedia(
            @Qualifier("PrefixesStorageDBPedia") PrefixesStorage prefixesStorage,
            @Qualifier("SparqlHttpClientDBPedia") SparqlHttpClient sparqlHttpClient,
            UriStorage uriStorage
    ) {
        this.prefixesStorage = prefixesStorage;
        this.sparqlHttpClient = sparqlHttpClient;
        this.uriStorage = uriStorage;
    }

    //returns suitable triples for question
    @Override
    public List<TripleDto> getSuitableTriplesStepOne(String entityUri) {

        final QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(sparqlHttpClient.getEndpointUrl(),
                PrefixesStorage.generatePrefixQueryString(prefixesStorage.getReplaceMap()) +
                        "select DISTINCT coalesce(?subjectLabelLang1, ?subjectLabelLang2) as ?subjectLabel, " +
                        "?predicate, " +
                        "coalesce(?predicateLabelLang1, ?predicateLabelLang2) as ?predicateLabel, " +
                        "?object, " +
                        "coalesce(coalesce(?objectLabelLang1, ?objectLabelLang2), ?object) as ?objectLabel where {\n" +
                        "  <" + entityUri + "> ?predicate ?object .\n" +
                        "\n" +
                        "  OPTIONAL {\n" +
                        "    <" + entityUri + "> rdfs:label ?subjectLabelLang1 .\n" +
                        "    FILTER(langMatches(lang(?subjectLabelLang1), \"ru\")) .\n" +
                        "  }\n" +
                        "  <" + entityUri + "> rdfs:label ?subjectLabelLang2 .\n" +
                        "  FILTER(langMatches(lang(?subjectLabelLang2), \"en\")) .\n" +
                        "\n" +
                        "  OPTIONAL {\n" +
                        "    ?object rdfs:label ?objectLabelLang1 .\n" +
                        "    FILTER(langMatches(lang(?objectLabelLang1), \"ru\")) .\n" +
                        "  }\n" +
                        "\n" +
                        "  OPTIONAL {\n" +
                        "    ?object rdfs:label ?objectLabelLang2 .\n" +
                        "    FILTER(langMatches(lang(?objectLabelLang2), \"en\")) .\n" +
                        "  }\n" +
                        "\n" +
                        "  OPTIONAL {\n" +
                        "    ?predicate rdfs:label ?predicateLabelLang1 . \n" +
                        "    FILTER(langMatches(lang(?predicateLabelLang1), \"ru\")) .\n" +
                        "  }\n" +
                        "\n" +
                        "  ?predicate rdfs:label ?predicateLabelLang2 .\n" +
                        "  FILTER(langMatches(lang(?predicateLabelLang2), \"en\")) ." +
                        "}\n" +
                        "limit 3000"
        );

        System.out.println(queryEngineHTTP.getQueryString());

        List<TripleDto> results = new ArrayList<>();
        try {
            ResultSet resultSet = queryEngineHTTP.execSelect();

            while (resultSet.hasNext()) {
                QuerySolution result = resultSet.next();

                EntityDto subject = new EntityDto();
                subject.setUri(entityUri);
                subject.setLabel(result.getLiteral("subjectLabel").getLexicalForm());

                EntityDto predicate = new EntityDto();
                predicate.setUri(result.get("predicate").toString());

                if (uriStorage.getBlackList().contains(predicate.getUri())) {
                    continue;
                }

                Literal predicateLabel = result.getLiteral("predicateLabel");
                if (predicateLabel != null) {
                    predicate.setLabel(predicateLabel.getLexicalForm());
                }

                EntityDto object = new EntityDto();
                object.setUri(result.get("object").toString());
                RDFNode objectLabel = result.get("objectLabel");
                if (objectLabel.isLiteral()) {
                    object.setLabel(objectLabel.asLiteral().getLexicalForm());
                } else {
                    object.setLabel(objectLabel.toString());
                }

                TripleDto currentTriple = new TripleDto();
                currentTriple.setSubject(subject);
                currentTriple.setPredicate(predicate);
                currentTriple.setObject(object);
                results.add(currentTriple);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        queryEngineHTTP.close();
        return results;
    }

    @Override
    public List<TripleDto> getSuitableTriplesStepTwo(String entityUri) {

        final QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(sparqlHttpClient.getEndpointUrl(),
                PrefixesStorage.generatePrefixQueryString(prefixesStorage.getReplaceMap()) +
                        "select DISTINCT ?subject," +
                        "coalesce(?subjectLabelLang1, ?subjectLabelLang2) as ?subjectLabel, " +
                        "?predicate, " +
                        "coalesce(?predicateLabelLang1, ?predicateLabelLang2) as ?predicateLabel, " +
                        "coalesce(?objectLabelLang1, ?objectLabelLang2) as ?objectLabel " +
                        " where {\n" +
                        "  ?subject ?predicate <" + entityUri + "> .\n" +
                        "\n" +
                        "  OPTIONAL {\n" +
                        "    <" + entityUri + "> rdfs:label ?objectLabelLang1 .\n" +
                        "    FILTER(langMatches(lang(?objectLabelLang1), \"ru\")) .\n" +
                        "  }\n" +
                        "  <" + entityUri + "> rdfs:label ?objectLabelLang2 .\n" +
                        "  FILTER(langMatches(lang(?objectLabelLang2), \"en\")) .\n" +
                        "\n" +
                        "  OPTIONAL {\n" +
                        "    ?subject rdfs:label ?subjectLabelLang1 .\n" +
                        "    FILTER(langMatches(lang(?subjectLabelLang1), \"ru\")) .\n" +
                        "  }\n" +
                        "\n" +
                        "  ?subject rdfs:label ?subjectLabelLang2 .\n" +
                        "  FILTER(langMatches(lang(?subjectLabelLang2), \"en\")) .\n" +
                        "\n" +
                        "  OPTIONAL {\n" +
                        "    ?predicate rdfs:label ?predicateLabelLang1 . \n" +
                        "    FILTER(langMatches(lang(?predicateLabelLang1), \"ru\")) .\n" +
                        "  }\n" +
                        "\n" +
                        "  ?predicate rdfs:label ?predicateLabelLang2 .\n" +
                        "  FILTER(langMatches(lang(?predicateLabelLang2), \"en\")) ." +
                        "}\n" +
                        "limit 3000"
        );

        System.out.println(queryEngineHTTP.getQueryString());

        List<TripleDto> results = new ArrayList<>();
        try {
            ResultSet resultSet = queryEngineHTTP.execSelect();

            while (resultSet.hasNext()) {
                QuerySolution result = resultSet.next();

                EntityDto subject = new EntityDto();
                subject.setUri(result.get("subject").toString());
                subject.setLabel(result.getLiteral("subjectLabel").getLexicalForm());

                EntityDto predicate = new EntityDto();
                predicate.setUri(result.get("predicate").toString());

                if (uriStorage.getBlackList().contains(predicate.getUri())) {
                    continue;
                }

                Literal predicateLabel = result.getLiteral("predicateLabel");
                if (predicateLabel != null) {
                    predicate.setLabel(predicateLabel.getLexicalForm());
                }

                EntityDto object = new EntityDto();
                object.setUri(entityUri);
                object.setLabel(result.getLiteral("objectLabel").getLexicalForm());

                TripleDto currentTriple = new TripleDto();
                currentTriple.setSubject(subject);
                currentTriple.setPredicate(predicate);
                currentTriple.setObject(object);
                results.add(currentTriple);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        queryEngineHTTP.close();
        return results;
    }

    //returns suitable triples for question
    @Override
    public String getRangeOfPredicate(String predicateUri) {
        final QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(sparqlHttpClient.getEndpointUrl(),
                PrefixesStorage.generatePrefixQueryString(prefixesStorage.getReplaceMap()) +
                        "select ?range where {\n" +
                        "  <" + predicateUri + "> rdfs:range ?range .\n" +
                        "}"
        );

        System.out.println(queryEngineHTTP.getQueryString());

        try {
            ResultSet resultSet = queryEngineHTTP.execSelect();

            if (resultSet.hasNext()) {
                QuerySolution result = resultSet.next();

                return result.get("range").toString();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        queryEngineHTTP.close();
        return null;
    }
}
