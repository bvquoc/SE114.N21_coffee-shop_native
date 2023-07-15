package fireclient

import (
	"context"
	"log"

	"cloud.google.com/go/firestore"
)

func (c *FirebaseClient) GetDocument(collection, documentID string) (*firestore.DocumentSnapshot, error) {
	docRef := c.client.Collection(collection).Doc(documentID)
	docSnapshot, err := docRef.Get(context.Background())
	if err != nil {
		log.Printf("Failed to get document %s: %v", documentID, err)
		return nil, err
	}
	return docSnapshot, nil
}

func documentToMap(doc *firestore.DocumentSnapshot) (map[string]interface{}, error) {
	data := make(map[string]interface{})
	if err := doc.DataTo(&data); err != nil {
		return nil, err
	}
	return data, nil
}

func (c *FirebaseClient) GetDocumentMap(collection, documentID string) (map[string]interface{}, error) {
	docSnapshot, err := c.GetDocument(collection, documentID)
	if err != nil {
		return nil, err
	}
	return documentToMap(docSnapshot)
}

// CreateDocument creates a new document with a generated ID in the specified collection.
func (c *FirebaseClient) CreateDocument(collection string, data map[string]interface{}) (string, error) {
	docRef, _, err := c.client.Collection(collection).Add(context.Background(), data)
	if err != nil {
		log.Printf("Failed to create document: %v", err)
		return "", err
	}
	return docRef.ID, nil
}

func (c *FirebaseClient) CreateDocumentWithId(collection string, ID string, data map[string]interface{}) error {
	docRef := c.client.Collection(collection).Doc(ID)
	_, err := docRef.Set(context.Background(), data)
	if err != nil {
		log.Printf("Failed to create document: %v", err)
		return err
	}
	return nil
}

// UpdateDocument updates an existing document in the specified collection with the given document ID.
func (c *FirebaseClient) UpdateDocument(collection, documentID string, data map[string]interface{}) error {
	docRef := c.client.Collection(collection).Doc(documentID)

	updates := make([]firestore.Update, 0, len(data))
	for key, value := range data {
		updates = append(updates, firestore.Update{
			Path:  key,
			Value: value,
		})
	}

	_, err := docRef.Update(context.Background(), updates)
	if err != nil {
		log.Printf("Failed to update document %s: %v", documentID, err)
		return err
	}

	return nil
}

// DeleteDocument deletes an existing document from the specified collection with the given document ID.
func (c *FirebaseClient) DeleteDocument(collection, documentID string) error {
	docRef := c.client.Collection(collection).Doc(documentID)
	_, err := docRef.Delete(context.Background())
	if err != nil {
		log.Printf("Failed to delete document %s: %v", documentID, err)
		return err
	}
	return nil
}
