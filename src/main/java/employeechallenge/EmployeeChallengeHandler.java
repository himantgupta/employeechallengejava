package employeechallenge;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

public class EmployeeChallengeHandler implements RequestHandler<Employee, String> {

	private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "Employee";
    private Regions REGION = Regions.US_WEST_2;
    private AmazonDynamoDBClient client;
    List<Employee> response;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String handleRequest(Employee input, Context context) {
       context.getLogger().log("Entering");
       this.initDynamoDbClient();
       // -----------------------Call the method to load initial data ----------------------------------------
       try {
			loadInitData("initEmployeesdata.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
        context.getLogger().log("Loaded data in DB");
       
        // -----------------------Use mapper for employee table ----------------------------------------
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        HashMap<String, String> attributeNames = new HashMap<String, String>();
        attributeNames.put("#st", "employeeStatus");
     
        HashMap<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
        attributeValues.put(":val", new AttributeValue().withS("Active"));
     
        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#st =:val")
                .withExpressionAttributeNames(attributeNames)
                .withExpressionAttributeValues(attributeValues);
     
        List<Employee> response = mapper.scan(Employee.class, dynamoDBScanExpression);
        String finalResponse = setResponse(response);
           
        return finalResponse;
    }
    
    
    // -----------------------Load Initial Data ----------------------------------------
    private void loadInitData(String filename) throws Exception
    {
    	JsonParser parser = new JsonFactory().createParser(new File(filename));

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();

        ObjectNode currentNode;
        Table table = this.dynamoDb.getTable(DYNAMODB_TABLE_NAME);
        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();

            int employeeId = currentNode.path("employeeId").asInt();
            String employeeStatus = currentNode.path("employeeStatus").asText();

            try {
                
            	table.putItem(
        	            new PutItemSpec().withItem(new Item()
            	            	.withInt("employeeId", currentNode.path("employeeId").asInt())
            	            	.withString("employeeStatus", currentNode.path("employeeStatus").asText())
            	    	        .withString("employeeFName", currentNode.path("employeeFName").asText())
            	    	        .withString("employeeMName", currentNode.path("employeeMName").asText())
            	                .withString("employeeLName", currentNode.path("employeeLName").asText())
            	                .withString("employeeBDay", currentNode.path("employeeBDay").asText())
            	                .withString("employeeJoinDate", currentNode.path("employeeJoinDate").asText())));
                
                System.out.println("PutItem succeeded: " + employeeId + " " + employeeStatus);
            }
            catch (Exception e) {
                System.err.println("Unable to add employee: " + employeeId + " " + employeeStatus);
                System.err.println(e.getMessage());
                break;
            }
        }
        parser.close();
    }
 
    // -----------------------INITIATE DB CLIENT ----------------------------------------
    public void initDynamoDbClient() {
        client = new AmazonDynamoDBClient();
        client.setRegion(com.amazonaws.regions.Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
    }

    // -----------------------Generate JSON format/ User overridden toString method -----
    public String setResponse(List<Employee> employeeList) {
    	Employee e;
    	String response ="Employee [";
		int count =0;
		Iterator<Employee> employeeIterator = employeeList.iterator();
		
		while (employeeIterator.hasNext()) {
			e = employeeIterator.next();
			if(count >0) 
				response +=",";
			response += e.toString();
			count++;	
		}
		response += "]";
		return response;
	}
}
