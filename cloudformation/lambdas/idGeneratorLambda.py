import boto3

def lambda_handler(event, context):
    dynamodb = boto3.client('dynamodb')
    table_name = 'employees'

    try:
        response = dynamodb.scan(TableName=table_name)

        if not response['Items']:
            next_id = 1
        else:
            print(response)
            max_id = max([int(item['id']['N']) for item in response['Items']])
            next_id = max_id + 1

        print('Next id:', next_id)
        return {
            'statusCode': 200,
            'body': {
                'next_id': next_id
            }
        }
    except Exception as e:
        return {
            'statusCode': 500,
            'body': str(e)
        }
