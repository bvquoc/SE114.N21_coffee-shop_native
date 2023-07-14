package models

type Store struct {
	Name    string  `json:"shortName"`
	Address Address `json:"address"`
}
