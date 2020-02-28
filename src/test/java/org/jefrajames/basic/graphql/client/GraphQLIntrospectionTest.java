/*
 * Copyright 2019 JF James.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jefrajames.basic.graphql.client;

import org.jefrajames.basic.graphql.client.GraphQLClient;
import org.jefrajames.basic.graphql.client.GraphQLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonPointer;
import javax.json.JsonValue;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This test is based on the SuperHeroDatabase. Do not forget to run "java -jar
 * superHeroServer.jar". Before running it.
 *
 * @author JF James
 */
public class GraphQLIntrospectionTest {

    // Jsonb class is multi-threaded and recommended to be reused
    private static Jsonb jsonb;

    private static URL graphqlEndpoint;

    private static Properties CONFIG = new Properties();

    @BeforeClass
    public static void beforeClass() throws MalformedURLException, IOException {

        CONFIG.load(GraphQLIntrospectionTest.class.getClassLoader().getResourceAsStream("graphql-config.properties"));
        graphqlEndpoint = new URL (CONFIG.getProperty("endpoint"));
        // graphqlEndpoint = new URL("http://localhost:9080/MPGraphQLSample/graphql");

        jsonb = JsonbBuilder.create();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (jsonb != null) {
            jsonb.close();
        }
    }

    public static class GraphQLType {

        public String name;
        public String kind;
    }

    private static boolean checkTeamInputType(List<GraphQLType> graphqlTypes) {
        for (int i = 0; i < graphqlTypes.size(); i++) {
            if (graphqlTypes.get(i).name.equals("TeamInput") && graphqlTypes.get(i).kind.equals("INPUT_OBJECT")) {
                return true;
            }
        }

        return false;
    }

    @Test
    public void checkExpectedTypes() {

        // No HTTP header here
        GraphQLClient client = new GraphQLClient(graphqlEndpoint);

        try {
            List<GraphQLType> graphqlTypes = client.execute(CONFIG.getProperty("allTypeNames"), (JsonObject jsonResponse) -> {
                JsonPointer pointer = Json.createPointer("/__schema/types");
                JsonValue value = pointer.getValue(jsonResponse);
                return jsonb.fromJson(value.toString(), new ArrayList<GraphQLType>() {
                }.getClass().getGenericSuperclass());
            });

            graphqlTypes.forEach(t -> System.out.println(t.name + ", " + t.kind));

            assertTrue(checkTeamInputType(graphqlTypes));

        } catch (GraphQLException | IOException ex) {
            Logger.getLogger(GraphQLIntrospectionTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }

    }

}
