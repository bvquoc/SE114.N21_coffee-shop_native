package fireclient

import (
	"context"

	"firebase.google.com/go/auth"
)

func (c *FirebaseClient) CreateUser(email, password, name string) (*auth.UserRecord, error) {
	params := (&auth.UserToCreate{}).
		Email(email).
		Password(password).
		DisplayName(name)

	user, err := c.auth.CreateUser(context.Background(), params)
	if err != nil {
		return nil, err
	}

	return user, nil
}
