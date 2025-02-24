# Sols Backend

## API

Base url `http://localhost:8080`

### Get All Notes

Request Method: GET

URL: `/api/notes`
Returns a list of all notes.

```json
[
    {
        "id": "65abcd123456",
        "text": "Meeting with team",
        "date": "2024-02-21",
        "time": "10:00:00",
        "tags": ["meeting", "work"],
        "image": "base64_encoded_image_data"
    }
]
```

### Add a Note

Request Method: POST

URL: `/api/notes/add`

Headers

- Content-Type: multipart/form-data
- Body (Form Data)
  - text (String, required) - Note content
  - date (String, required) - Date in yyyy-MM-dd format
  - time (String, required) - Date in HH:mm:ss format
  - tags (List, optional) - Tags related to the note
  - image (MultipartFile, optional) - Image file (Max: 10MB)

```json
{
    "text": "Meeting with team",
    "date": "2024-02-21T",
    "time": "10:00:00",
    "tags": ["meeting", "work"],
    "image": "upload file"
}
```

### Get Notes by Date

Request Method: GET

URL: `/api/notes/{date}`

Path Parameter: date (String, required) - Format: yyyy-MM-dd

Example Request `/api/notes/2024-02-21`

### Get Images in each month

Request Method: GET

URL: `/api/notes/calendar/{year}/{month}`

Path Parameter: year (int) and month {int}

Example Request `/api/notes/calendar/2025/2`

### Delete All Notes

Request Method: DELETE
URL: `/api/notes/delete`
