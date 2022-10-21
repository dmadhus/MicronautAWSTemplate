package com.bytestream;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.User;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.HashMap;

public class AppStack extends Stack {

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        //you should create and define users here
        //I already have a user created, and I am simply getting a reference
        var myUser = User.fromUserName(this, "erickUser", "ericktest");

        //create application specific roles and grant users accesse to jump into the roles
        var appRole = new Role(this, "appRole", RoleProps.builder()
                .assumedBy(myUser).roleName("appRole").build());
        //can get a policy statement from json or use a builder
        //add cloudwatch permissions
        var cwPol = PolicyStatement.Builder.create().actions(Arrays.asList("cloudwatch:PutMetricData")).resources(Arrays.asList("*")).build();
        appRole.addToPolicy(cwPol);

        //end hashmap of variables is helpful to pass down ids to other methods in more complex stacks.
        var environmentVariables = new HashMap<String, String>();
        var table = Table.Builder.create(this, "micronautapptable")
                .tableName("people")
                //if you do not give a name then it will be auto generated, that's fine for passing cdk references but bad for the app
                .partitionKey(Attribute.builder()
                        .name("firstName")
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name("lastName")
                        .type(AttributeType.STRING)
                        .build())
                .build();

        environmentVariables.put("DYNAMODB_TABLE_NAME", table.getTableName());
        //let the application read and write to function
        table.grantReadWriteData(appRole);

    }


}