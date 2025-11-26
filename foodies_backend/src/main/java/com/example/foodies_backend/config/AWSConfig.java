package com.example.foodies_backend.config;

// Importing necessary Spring and AWS SDK classes
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

// Marking this class as a configuration class for Spring
@Configuration
public class AWSConfig {

// Injecting the AWS access key from the application properties
@Value("${aws.access.key}")
private String accessKey;

// Injecting the AWS secret key from the application properties
@Value("${aws.secret.key}")
private String secretKey;

// Injecting the AWS region from the application properties
@Value("${aws.region}")
private String region;

// Defining a bean for the S3Client to interact with AWS S3
@Bean
public S3Client s3Client() {
	return S3Client.builder()
			// Setting the AWS region for the S3 client
			.region(Region.of(region))
			// Providing static credentials (access key and secret key) for authentication
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
			// Building and returning the S3 client instance
			.build();
}
}