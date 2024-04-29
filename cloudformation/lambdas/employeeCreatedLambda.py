import boto3
import json

def lambda_handler(event, context):
    sns_client = boto3.client('sns')

    for record in event['Records']:
        if record['eventName'] == 'INSERT':
            dynamodb_item = record['dynamodb']['NewImage']
            formatted_message = format_dynamodb_item(dynamodb_item)
            sns_client.publish(
                TopicArn='arn:aws:sns:eu-central-1:992382636162:EmployeeCreated',
                Message=formatted_message,
                Subject='New Employee created in DynamoDB'
            )
    return {
        'statusCode': 200,
        'body': json.dumps('Notification sent successfully')
    }

def format_dynamodb_item(item):
    formatted_item = ""
    for key, value in item.items():
        if 'S' in value:
            formatted_item += f"{key}: {value['S']}\n"
        elif 'N' in value:
            formatted_item += f"{key}: {value['N']}\n"
    return formatted_item
