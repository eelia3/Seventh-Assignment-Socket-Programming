# Report.md

## Theoretical Questions 📝

### 1. Plain String Format

**Example:**  
`LOGIN|user1|pass123`

**Pros:**
- It's very simple and quick to implement  
- Doesn't need any special library  
- You can understand what is sent just by looking at it  

**Cons:**
- If the character `|` is inside the username or password, it gets messed up  
- You have to parse it manually, which can go wrong  
- Not good for nested or complex data  

**How to Parse:**  
You can use `split("|")`, but you need to be careful if the delimiter appears in the actual data.

---

### 2. Serialized Java Object

**Pros:**
- You can send full objects, even complex ones  
- No need to manually handle the structure  
- Easy to use between Java apps

**Cons:**
- Only works if both client and server are Java  
- Not readable by humans  
- Version mismatch can cause problems  

**Cross-language Compatibility:**  
It won’t work with non-Java clients like Python, because serialization format is specific to Java.

---

### 3. JSON

**Example:**  
```json
{ "username": "user1", "password": "pass123" }
```

**Pros:**
- Works with almost every language  
- Human-readable and structured  
- Great for web and APIs

**Cons:**
- Slightly bigger in size than plain string  
- Needs a library to parse (like Gson in Java)  

**Cross-language Compatibility:**  
Yes, it works easily with Python, JavaScript, and other languages.

---
