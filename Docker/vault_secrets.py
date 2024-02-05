import requests
import json
import yaml

# Function to make an HTTP API call with Authorization header
def make_api_call(api_url, bearer_token):
    headers = {"Authorization": f"Bearer {bearer_token}"}
    response = requests.get(api_url, headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Error: Unable to fetch data from API. Status code: {response.status_code}")
        return None

# Function to update a JSON file
def update_json_file(json_filename, api_data):
    try:
        with open(json_filename, 'r') as json_file:
            json_data = json.load(json_file)
    except FileNotFoundError:
        print(f"Error: JSON file '{json_filename}' not found.")
        exit(1)

    json_data['smtpServer']['password'] = api_data['data']['data']['smtp.password']
    
    json_data['clients'][2]['secret'] = api_data['data']['data']['keycloak-rest.client-secret']
    json_data['clients'][6]['secret'] = api_data['data']['data']['spring.security.oauth2.client.registration.keycloak.client-secret']

    with open(json_filename, 'w') as json_file:
        json.dump(json_data, json_file, indent=2)

# Function to update a YAML file
def update_yaml_file(yaml_filename, api_data):
    try:
        with open(yaml_filename, 'r') as yaml_file:
            yaml_data = yaml.load(yaml_file)
    except FileNotFoundError:
        print(f"Error: YAML file '{yaml_filename}' not found.")
        exit(1)

    ##### POSTGRES #####
    yaml_data['services']['postgres']['environment']['POSTGRES_DB'] = api_data['data']['data']['spring.datasource.url'].split('/')[-1]
    yaml_data['services']['postgres']['environment']['POSTGRES_USER'] = api_data['data']['data']['spring.datasource.username']
    yaml_data['services']['postgres']['environment']['POSTGRES_PASSWORD'] = api_data['data']['data']['spring.datasource.password']

    ##### KEYCLOAK #####
    yaml_data['services']['keycloak']['environment']['KC_DB'] = api_data['data']['data']['spring.datasource.url'].split('/')[-1]
    yaml_data['services']['keycloak']['environment']['KC_DB_URL'] = api_data['data']['data']['spring.datasource.url']
    yaml_data['services']['keycloak']['environment']['KC_DB_USERNAME'] = api_data['data']['data']['spring.datasource.username']
    yaml_data['services']['keycloak']['environment']['KC_DB_PASSWORD'] = api_data['data']['data']['spring.datasource.password']
    yaml_data['services']['keycloak']['environment']['KEYCLOAK_ADMIN'] = api_data['data']['data']['keycloak.admin.username']
    yaml_data['services']['keycloak']['environment']['KEYCLOAK_ADMIN_PASSWORD'] = api_data['data']['data']['keycloak.admin.password']

    with open(yaml_filename, 'w') as yaml_file:
        yaml.dump(yaml_data, yaml_file, default_flow_style=False)


api_url = "https://studyappvault.ddns.net/v1/secret/data/studyapp"
json_filename = "realm/realm-export.json"
yaml_filename = "docker-compose_studyapp.yml"
bearer_token = "VAULT_TOKEN"

# Make API call with Authorization header
api_data = make_api_call(api_url, bearer_token)

# Update JSON file
if api_data:
    update_json_file(json_filename, api_data)
    print(f"JSON file '{json_filename}' updated successfully.")

# Update YAML file
if api_data:
    update_yaml_file(yaml_filename, api_data)
    print(f"YAML file '{yaml_filename}' updated successfully.")
