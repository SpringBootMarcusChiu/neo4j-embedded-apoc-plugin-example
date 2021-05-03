package com.neo4j.example.springdataneo4jintroapp;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
public class SpringDataNeo4jIntroAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataNeo4jIntroAppApplication.class, args);
	}

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public void run(String... args) throws Exception {
		var session = sessionFactory.openSession();
		var result = session.query("CALL apoc.help(\"apoc\")", new HashMap<>());
		List<Map<String, Object>> dataList = StreamSupport.stream(result.spliterator(), false)
				.collect(Collectors.toList());
		System.out.println(dataList.toString());
	}
}
