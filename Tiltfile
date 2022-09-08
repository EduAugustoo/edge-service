custom_build(
    ref = 'edge-service',
    command = '.\\mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=${env.EXPECTED_REF}',
    deps = ['pom.xml', 'src']
)

k8s_yaml(kustomize('k8s'))

k8s_resource('edge-service', port_forwards=['9000'])