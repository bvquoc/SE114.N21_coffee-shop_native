package datastruct

type SetOfString map[string]struct{}

func (s SetOfString) Add(item string) {
	s[item] = struct{}{}
}

func (s SetOfString) Remove(item string) {
	delete(s, item)
}

func (s SetOfString) Contains(item string) bool {
	_, exists := s[item]
	return exists
}

func (s SetOfString) Size() int {
	return len(s)
}
