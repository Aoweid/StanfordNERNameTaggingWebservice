import boto3
import json
import sys

#This script creates resources and methods for the web services

#Input to the script: API ID, Root Resource ID, Stage and ELB DNS

client = boto3.setup_default_session(region_name='us-east-1')
ApiId = sys.argv[1]
RootResourceId = sys.argv[2]
stage = sys.argv[3]
elb_dns = sys.argv[4]

resourceName = 'tagnameentityusingstanfordner'

client = boto3.client('apigateway')

#Check if the resource exits
response = client.get_resources(
    restApiId=ApiId,
    limit=500
)
flag = 0
resources = response['items']

#If resource exists, set the flag
for resource in resources:
        if resourceName == resource.get('pathPart'):
                resourceID = resource.get('id')
                flag = 1
                #If there is need to update the resource or methods, make necessary changes 
                #comment the above line and uncomment the below 2 lines
                #flag = 0
                #client.delete_resource(restApiId=ApiId,resourceId=resourceID)
                
#If resource doesn't exist, create resource               
if flag == 0:
        try:
                response = client.create_resource(restApiId=ApiId, parentId=RootResourceId, pathPart=resourceName)
        except Exception:
                print 'Resource Exists'
        else:
                resourceID = response.get('id')

#Create Method Request, Integration Request, Method Response and Integration response

                methodRequest = client.put_method(
                                    restApiId=ApiId,
                                    resourceId=resourceID,
                                    httpMethod='POST',
                                    authorizationType='NONE',
                                    requestParameters={
                                        'method.request.header.Accept' : True,
                                        'method.request.header.Content-Type' : True
                                    }
                                )
                integrationRequest = client.put_integration(
                                        restApiId=ApiId,
                                        resourceId=resourceID,
                                        httpMethod='POST',
                                        type='HTTP',
                                        passthroughBehavior='WHEN_NO_TEMPLATES',
                                        integrationHttpMethod='POST',
                                        uri='http://${stageVariables.TagNameEntityUsingStanfordNERUrl}',
                                        requestParameters={
                                            'integration.request.header.Accept' : 'method.request.header.Accept',
      		                                'integration.request.header.Content-Type' : 'method.request.header.Content-Type'
                                        }                     
                )
                methodResponse = client.put_method_response(
                                    restApiId=ApiId,
                                    resourceId=resourceID,
                                    httpMethod='POST',
                                    statusCode='200'
                                )
                integrationResponse = client.put_integration_response(
                                        restApiId=ApiId,
                                        resourceId=resourceID,
                                        httpMethod='POST',
                                        statusCode='200',
                                        responseParameters={
                                        }
                                        )

#Update the ELB DNS in the stage variables
method1 = '/StanfordNERNameTaggingWebservice/rest/StanfordNERNameTaggingWebservice'
client.update_stage( 
    restApiId=ApiId,
    stageName=stage,
    patchOperations=[
        {
            'op':'replace',
            'path':'/variables/TagNameEntityUsingStanfordNERUrl',
            'value':elb_dns + method1
        }
    ]
)

client.update_stage( 
    restApiId=ApiId,
    stageName='mock',
    patchOperations=[
        {
            'op':'replace',
            'path':'/variables/TagNameEntityUsingStanfordNERUrl',
            'value':'private-cb260-tagnameentityusingstanfordner.apiary-mock.com'
        }
    ]
)

deploymentResponse = None
while deploymentResponse is None:
    try:
        deploymentResponse=client.create_deployment(
                                restApiId=ApiId,
                                stageName=stage
        )
    except:
        pass
        


