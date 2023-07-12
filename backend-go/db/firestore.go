package db

import "cloud.google.com/go/firestore"

type FirestoreClient struct {
	client *firestore.Client
}
