package models

type User struct {
	Email        string
	AvatarURL    string
	CoverURL     string
	Name         string
	IsSuperAdmin bool
	IsAdmin      bool
	IsStaff      bool
	StaffOf      string
}

func NewUser(userInfo map[string]interface{}) *User {
	user := &User{
		Email:        "",
		AvatarURL:    "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg",
		CoverURL:     "https://img.freepik.com/free-vector/restaurant-mural-wallpaper_23-2148703851.jpg?w=740&t=st=1680897435~exp=1680898035~hmac=8f6c47b6646a831c4a642b560cf9b10f1ddf80fda5d9d997299e1b2f71fe4cb9",
		Name:         "Coffee Shop Employee",
		IsSuperAdmin: false,
		IsAdmin:      false,
		IsStaff:      false,
		StaffOf:      "",
	}

	fields := map[string]*string{
		"email":     &user.Email,
		"avatarURL": &user.AvatarURL,
		"coverURL":  &user.CoverURL,
		"name":      &user.Name,
		"staffOf":   &user.StaffOf,
	}

	boolFields := map[string]*bool{
		"isSuperAdmin": &user.IsSuperAdmin,
		"isAdmin":      &user.IsAdmin,
		"isStaff":      &user.IsStaff,
	}

	for key, value := range userInfo {
		if str, ok := fields[key]; ok {
			if strValue, ok := value.(string); ok {
				*str = strValue
			}
		} else if boolean, ok := boolFields[key]; ok {
			if boolValue, ok := value.(bool); ok {
				*boolean = boolValue
			}
		}
	}

	return user
}
