package fireclient

import (
	"context"
	"log"

	"firebase.google.com/go/auth"
)

func (c *FirebaseClient) CreateUser(email, password string) (*auth.UserRecord, error) {
	params := (&auth.UserToCreate{}).
		Email(email).
		Password(password)

	user, err := c.auth.CreateUser(context.Background(), params)
	if err != nil {
		log.Fatalf("Failed to create user: %v", err)
		return nil, err
	}

	return user, nil
}
