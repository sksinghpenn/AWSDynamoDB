package com.lifotech.aws.dynamodb;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class DynamoDBManager {

	private static Logger logger = Logger.getLogger(DynamoDBManager.class);

	private static AmazonDynamoDBClient amazonDynamoDBClient;

	public static void init() {
		logger.debug("starting init()");
		AWSCredentialsProvider awsCredentialsProvider = getAWSCredentialsProvoider();
		amazonDynamoDBClient = new AmazonDynamoDBClient(awsCredentialsProvider);
		logger.debug("returning from init()");
	}

	public TableDescription createTable(String tableName, String primaryKeyName, KeyType keyType, long readCapacity, long writeCapacity) {
		init();

		logger.debug("creating table : " + tableName);

		CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName).withKeySchema(new KeySchemaElement().withAttributeName(primaryKeyName).withKeyType(KeyType.HASH))
				.withAttributeDefinitions(new AttributeDefinition().withAttributeName(primaryKeyName).withAttributeType(ScalarAttributeType.S))
				.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacity).withWriteCapacityUnits(writeCapacity));
		TableDescription tableDescription = amazonDynamoDBClient.createTable(createTableRequest).getTableDescription();

		logger.debug("waiting for table " + tableName + " to become active");

		waitForTableToBecomeAvailable(tableName);

		logger.debug("Table " + tableName + " is active now -- returning its tableDescription");
		return tableDescription;
	}

	public void addItem(String tableName, Map<String, AttributeValue> item) {
		init();

		logger.debug("adding item into table " + tableName);

		PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
		amazonDynamoDBClient.putItem(putItemRequest);

		logger.debug("added item " + item + " into table " + tableName);
	}

	public static ScanResult getBookPublishedBasedOnPublishedYearCondition(String year, String attributeName,  ComparisonOperator comparisonOperator, String tableName) {

		// scan items for books published after 1991
		Condition condition = new Condition().withComparisonOperator(comparisonOperator).withAttributeValueList(new AttributeValue().withN("1991"));

		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();

		scanFilter.put(attributeName, condition);

		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		
		return amazonDynamoDBClient.scan(scanRequest);

	}

	private static void waitForTableToBecomeAvailable(String tableName) {

		long endTime = System.currentTimeMillis() + 10 * 60 * 1000; // 10 min

		while (System.currentTimeMillis() < endTime) {
			try {
				Thread.sleep(20 * 1000); // sleep 20 seconds
			} catch (InterruptedException e) {
			}

			try {
				DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
				DescribeTableResult describeTableResult = amazonDynamoDBClient.describeTable(describeTableRequest);
				TableDescription tableDesription = describeTableResult.getTable();

				String tableStatus = tableDesription.getTableStatus();

				if (tableStatus.equals("ACTIVE")) {
					return;
				}
			} catch (AmazonServiceException e) {
				if ((e.getErrorCode().equalsIgnoreCase("ResourceNotFoundException")))
					throw e;
			}
		}

		throw new RuntimeException("The table " + tableName + " could not become active");

	}

	private static AWSCredentialsProvider getAWSCredentialsProvoider() {
		return new ClasspathPropertiesFileCredentialsProvider();
	}

}
