package fireclient

import (
	"context"
	"log"

	firebase "firebase.google.com/go"
)

func NewFirestoreClient(projectID string) (*FirebaseClient, error) {
	ctx := context.Background()

	// Initialize the Firebase app with credentials file.
	conf := &firebase.Config{ProjectID: projectID}
	app, err := firebase.NewApp(ctx, conf)
	if err != nil {
		log.Fatalf("Failed to create Firebase app: %v", err)
		return nil, err
	}

	// Initialize Firestore client.
	client, err := app.Firestore(ctx)
	if err != nil {
		log.Fatalf("Failed to create Firestore client: %v", err)
		return nil, err
	}

	// Initialize Firebase Authentication client.
	authClient, err := app.Auth(ctx)
	if err != nil {
		log.Fatalf("Failed to create Auth client: %v", err)
		return nil, err
	}

	return &FirebaseClient{
		client: client,
		auth:   authClient,
	}, nil
}

func (c *FirebaseClient) Close() {
	c.client.Close()
}

// func NewFirestoreClient(projectID string) (*FirebaseClient, error) {
// 	ctx := context.Background()

// 	// Initialize the Firebase app with credentials file.
// 	conf := &firebase.Config{ProjectID: projectID}
// 	app, err := firebase.NewApp(ctx, conf)
// 	if err != nil {
// 		log.Fatalf("Failed to create Firebase app: %v", err)
// 		return nil, err
// 	}

// 	// Initialize Firestore client.
// 	client, err := app.Firestore(ctx)
// 	if err != nil {
// 		log.Fatalf("Failed to create Firestore client: %v", err)
// 		return nil, err
// 	}

// 	return &FirebaseClient{
// 		client: client,
// 	}, nil
// }

// func (c *FirebaseClient) Close() {
// 	c.client.Close()
// }
