package com.lifotech.aws.dynamodb.client;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.lifotech.aws.dynamodb.DynamoDBManager;

public class DynamoDBManagerClient {

	public static void main(String[] args) {

		try {
			DynamoDBManager dynamoDBManager = new DynamoDBManager();

			String tableName = "Books";
			String primaryKeyName = "BookId";
			KeyType primaryKeyType = KeyType.HASH;
			long readCapacity = 1L;
			long writeCapacity = 1L;

			try {
				TableDescription tableDesc = dynamoDBManager.createTable(tableName, primaryKeyName, primaryKeyType, readCapacity, writeCapacity);
				System.out.println("Table Description " + tableDesc);
				System.out.println("Table " + tableDesc.getTableName() + " is created");
			} catch (AmazonServiceException e) {
				System.out.println("There was problem in creating the table " + e.getMessage());
			}

			// add item
			Map<String, AttributeValue> item1 = new HashMap<String, AttributeValue>();
			item1.put("BookId", new AttributeValue("1"));
			item1.put("BookName", new AttributeValue("Came With Winds"));
			item1.put("AuthorName", new AttributeValue("James Googling"));
			item1.put("PublishedYear", new AttributeValue().withN("1991"));

			dynamoDBManager.addItem(tableName, item1);
			System.out.println("item added into the table");

			// add another item
			Map<String, AttributeValue> item2 = new HashMap<String, AttributeValue>();
			item2.put("BookId", new AttributeValue("2"));
			item2.put("BookName", new AttributeValue("Tiger Beds"));
			item2.put("AuthorName", new AttributeValue("Jimmy Simpson"));
			item2.put("PublishedYear", new AttributeValue().withN("1991"));

			dynamoDBManager.addItem(tableName, item2);
			System.out.println("item added into the table");

			// add another item
			Map<String, AttributeValue> item3 = new HashMap<String, AttributeValue>();
			item3.put("BookId", new AttributeValue("3"));
			item3.put("BookName", new AttributeValue("Loin Dreams"));
			item3.put("AuthorName", new AttributeValue("Booby Taylor"));
			item3.put("PublishedYear", new AttributeValue().withN("1992"));

			dynamoDBManager.addItem(tableName, item3);
			System.out.println("item added into the table");
			
			
			ScanResult scanResult = DynamoDBManager.getBookPublishedBasedOnPublishedYearCondition("1991",  "PublishedYear", ComparisonOperator.GT, "Books");
			
			
			List<Map<String, AttributeValue>> items = scanResult.getItems();
			
			System.out.println("Number of items found in scanning with matching condition : " + items.size());
			
			for (Map<String, AttributeValue> item: items) {
				System.out.println(" item " + item);
			}
			
			System.out.println("all items scaned");

		} catch (AmazonServiceException e) {
			System.out.println("Caught AmazonServiceException which means your request made it to Amazon S3 " + "however, it was rejetced with an error repsonse for some reason");
			System.out.println("AWS Error message " + e.getMessage());
			System.out.println("AWS Error Code " + e.getErrorCode());
			System.out.println("HTTP Status code " + e.getStatusCode());
			System.out.println("Error Type " + e.getErrorType());
			System.out.println("Request ID " + e.getRequestId());
		} catch (AmazonClientException e) {
			System.out.println("Caught AmazonServiceException which means your request either could not reach to Amazon S3 or " + "response could not be parsed");
			System.out.println("AWS Error message " + e.getMessage());
		} catch (RuntimeException e) {
			System.out.println("Caught RuntimeException");
			System.out.println("Error message : " + e.getMessage());
		}
	}
	
}
