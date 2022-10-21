package com.bytestream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;

import java.util.Map;

class AppStackTest {

    @Test
    void testAppStack() {

        var stack = new AppStack(new App(), "TestMicronautAppStack");

        //parse the stack json and test
        var template = Template.fromStack(stack);
        //we can inspect the template as a string
        System.out.println(template.toJSON());

        //can get objects and filter if you match the properties in the right structure to match the template
        var ddbTableAttributes = Map.of("Properties", Map.of("TableName", "people"));
        var ddbTable = template.findResources("AWS::DynamoDB::Table", ddbTableAttributes);
        printResource(ddbTable);

        //For validation taking the simple string of the queried object and doing string matching is probably sufficient
        // instead of looking into the nested map
        //each entry in the outer map is an object, make sure our query only found one object
        Assertions.assertEquals(1, ddbTable.size());
        var ddbTableStringDef = ddbTable.toString();
        Assertions.assertTrue(ddbTableStringDef.contains("TableName=people"));

    }

    void printResource(Map<String, Map<String, Object>> resource) {
        resource.forEach((a, b) -> {
            System.out.printf("***Start %s ****%n", a);
            b.forEach((c, d) -> {
                System.out.println(c + ":" + d);
            });

            System.out.printf("***End %s ****%n", a);
        });
    }
}
