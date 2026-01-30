workspace "NexaTech E-Commerce" "Component diagram for NexaTech Backend API - Optimized Layout" {

    !identifiers hierarchical

    model {
        customer = person "Customer" "User shopping via web or mobile"
        admin = person "Admin" "System administrator"

        nexatech = softwareSystem "NexaTech E-Commerce System" {
    description """
    A full-featured e-commerce platform supporting online shopping,
    product catalog management, order processing, secure payments,
    user authentication, and administrative operations for the NexaTech ecosystem.
    """

    backend = container "Backend API" {
        description "Handles business logic for the e-commerce platform"
        technology "Spring Boot / Node.js"

        authComponent = component "Auth Component" {
            description "Authentication and authorization (JWT, roles)"
            technology "Spring Security / Auth Middleware"
            tags "AuthLayer"
        }

        userComponent = component "User Component" {
            description "Manages users and profiles"
            technology "Service Layer"
            tags "BusinessLogic"
        }

        productComponent = component "Product Component" {
            description "Manages products and categories"
            technology "Service Layer"
            tags "BusinessLogic"
        }

        orderComponent = component "Order Component" {
            description "Handles order creation and order lifecycle"
            technology "Service Layer"
            tags "BusinessLogic"
        }

        paymentComponent = component "Payment Component" {
            description "Processes payments and handles gateway callbacks"
            technology "Integration Service"
            tags "Integration"
        }

        emailComponent = component "Email Component" {
            description "Sends emails via AWS SES"
            technology "Integration Service"
            tags "Integration"
        }

        persistenceComponent = component "Persistence Component" {
            description "Handles database access"
            technology "JPA / TypeORM"
            tags "DataAccess"
        }
    }

    database = container "Database" {
        description "Stores users, products, orders, and payments"
        technology "PostgreSQL"
        tags "Database"
    }
}


        momo = softwareSystem "MOMO Payment Gateway" {
            description "Vietnamese payment gateway"
            tags "External"
        }

        vnpay = softwareSystem "VNPAY Payment Gateway" {
            description "Vietnamese payment gateway"
            tags "External"
        }

        emailService = softwareSystem "AWS Email Service (SES)" {
            description "Email delivery service"
            tags "External"
        }

        customer -> nexatech.backend "Uses API" "HTTPS/REST"
        admin -> nexatech.backend "Manages system" "HTTPS/REST"

        customer -> nexatech.backend.authComponent "Authenticates"
        admin -> nexatech.backend.authComponent "Authenticates"
        nexatech.backend.authComponent -> nexatech.backend.userComponent "Validates credentials"

        customer -> nexatech.backend.productComponent "Browses products"
        customer -> nexatech.backend.orderComponent "Places orders"
        admin -> nexatech.backend.productComponent "Manages products"
        admin -> nexatech.backend.userComponent "Manages users"

        nexatech.backend.orderComponent -> nexatech.backend.productComponent "Checks inventory"
        nexatech.backend.orderComponent -> nexatech.backend.paymentComponent "Initiates payment"
        nexatech.backend.orderComponent -> nexatech.backend.emailComponent "Sends confirmation"

        nexatech.backend.paymentComponent -> momo "Create payment request" "HTTPS"
        nexatech.backend.paymentComponent -> vnpay "Create payment request" "HTTPS"

        momo -> nexatech.backend.paymentComponent "Payment callback / IPN" "HTTPS"
        vnpay -> nexatech.backend.paymentComponent "Payment callback / IPN" "HTTPS"

        nexatech.backend.emailComponent -> emailService "Sends emails" "AWS SDK"

        nexatech.backend.userComponent -> nexatech.backend.persistenceComponent "User data"
        nexatech.backend.productComponent -> nexatech.backend.persistenceComponent "Product data"
        nexatech.backend.orderComponent -> nexatech.backend.persistenceComponent "Order data"
        nexatech.backend.paymentComponent -> nexatech.backend.persistenceComponent "Payment data"

        nexatech.backend.persistenceComponent -> nexatech.database "JDBC/SQL"
    }

    views {

        component nexatech.backend "NexaTech-Component-Diagram" {
            title "Backend API – Component Diagram"
            include *
            autolayout lr
        }

        component nexatech.backend "NexaTech-Integration-View" {
            title "Backend API – Integration Overview"
            description "Integration components and their interactions with external systems"
            include nexatech.backend.paymentComponent nexatech.backend.emailComponent momo vnpay emailService
            autolayout lr
        }

        component nexatech.backend "NexaTech-Payment-Integration-View" {
            title "Backend API – Payment Integration"
            description "Payment request and callback flow with external payment gateways"
            include nexatech.backend.paymentComponent momo vnpay
            autolayout lr
        }

        styles {
            element "Person" {
                shape person
                background #ffffff
                color #08427b
                stroke #08427b
                strokeWidth 2
            }

            element "Software System" {
                shape box
                background #ffffff
                color #1168bd
                stroke #1168bd
                strokeWidth 2
            }

            element "Container" {
                shape box
                background #ffffff
                color #438dd5
                stroke #438dd5
                strokeWidth 2
            }

            element "AuthLayer" {
                shape component
                background #ffffff
                color #e74c3c
                stroke #e74c3c
                strokeWidth 3
            }

            element "BusinessLogic" {
                shape component
                background #ffffff
                color #27ae60
                stroke #27ae60
                strokeWidth 3
            }

            element "Integration" {
                shape component
                background #ffffff
                color #3498db
                stroke #3498db
                strokeWidth 3
            }

            element "DataAccess" {
                shape component
                background #ffffff
                color #f39c12
                stroke #f39c12
                strokeWidth 3
            }

            element "Database" {
                shape cylinder
                background #ffffff
                color #f39c12
                stroke #f39c12
                strokeWidth 3
            }

            element "External" {
                shape box
                background #ffffff
                color #8e44ad
                stroke #8e44ad
                strokeWidth 2
            }
        }

        theme default
    }
}
