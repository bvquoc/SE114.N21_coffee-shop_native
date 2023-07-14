package fireclient

import (
	"cloud.google.com/go/firestore"
	"firebase.google.com/go/auth"
)

type FirebaseClient struct {
	client *firestore.Client
	auth   *auth.Client
}
