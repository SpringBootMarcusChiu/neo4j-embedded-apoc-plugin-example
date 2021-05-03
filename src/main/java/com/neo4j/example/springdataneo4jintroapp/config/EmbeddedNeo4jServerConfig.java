package com.neo4j.example.springdataneo4jintroapp.config;

import apoc.coll.Coll;
import apoc.convert.Json;
import apoc.create.Create;
import apoc.help.Help;
import apoc.load.LoadCsv;
import apoc.load.LoadJson;
import apoc.load.Xml;
import apoc.meta.Meta;
import apoc.path.PathExplorer;
import apoc.refactor.GraphRefactoring;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.exceptions.KernelException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

@Configuration
public class EmbeddedNeo4jServerConfig {

    @Bean
    public GraphDatabaseService graphDatabaseService() {
        System.out.println("STARTING Embedded Neo4j");
        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(new File("embedded_neo4j_database"))
                .loadPropertiesFromFile("neo4j.conf")
                .build();
        GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService);
        registerProcedure(graphDb,
                Coll.class,
                apoc.map.Maps.class,
                Json.class,
                Create.class,
                apoc.date.Date.class,
                apoc.lock.Lock.class,
                LoadJson.class,
                LoadCsv.class,
                Xml.class,
                PathExplorer.class,
                Meta.class,
                GraphRefactoring.class,
                Help.class,
                apoc.periodic.Periodic.class);
        System.out.println("STARTED Embedded Neo4j");
        return graphDb;
    }

    public static void registerProcedure(GraphDatabaseService db, Class<?>... procedures) {
        GlobalProcedures globalProcedures = ((GraphDatabaseAPI) db).getDependencyResolver().resolveDependency(GlobalProcedures.class);
        for (Class<?> procedure : procedures) {
            try {
                globalProcedures.registerProcedure(procedure, true);
                globalProcedures.registerFunction(procedure, true);
                globalProcedures.registerAggregationFunction(procedure, true);
            } catch (KernelException e) {
                throw new RuntimeException("while registering " + procedure, e);
            }
        }
    }

    private static void registerShutdownHook(final DatabaseManagementService managementService) {
        // Registers a shutdown hook for the Neo4j instance so that it shuts
        // down nicely when the VM exits (even if you "Ctrl-C" the running application)
        Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
    }
}
