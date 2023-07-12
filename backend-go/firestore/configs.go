package firestore

import (
	"context"
	"log"

	firebase "firebase.google.com/go"
)

func NewFirestoreClient(projectID string) (*FirestoreClient, error) {
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

	return &FirestoreClient{
		client: client,
	}, nil
}

func (c *FirestoreClient) Close() {
	c.client.Close()
}
