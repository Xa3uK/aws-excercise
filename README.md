# Environment Variables

Before running the application, make sure to set the following environment variables:

- `AWS_ACCESS_KEY`: Your AWS access key.
- `AWS_SECRET_KEY`: Your AWS secret key.
- `AWS_DYNAMODB_TABLE_NAME`: The name of the DynamoDB table.
- `AWS_REGION`: The AWS region where your resources are deployed.
- `AWS_S3_BUCKET`: The name of the S3 bucket.
- `AWS_S3_ENDPOINT`: The endpoint URL for the S3 bucket.

These environment variables are required for the proper functioning of the application. Ensure they are correctly configured before deployment.

# GitHub Secrets

Additionally, ensure the following GitHub secrets are defined:

- `AWS_ACCESS_KEY`: Your AWS access key.
- `AWS_SECRET_KEY`: Your AWS secret key.
- `AWS_ACCOUNT_ID`: Your AWS account ID.
- `AWS_ECR_REPO_NAME`: The name of your ECR repository.
- `AWS_REGION`: The AWS region where your resources are deployed.
