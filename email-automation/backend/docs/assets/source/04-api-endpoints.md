# API Endpoints

## Health and Gmail

- `GET /api/health`
- `GET /api/gmail/status`
- `GET /api/gmail/emails`
- `GET /api/emails`

## Export

`GET /api/export` starts the export workflow. The optional `format` parameter defaults to `text`.

```text
http://localhost:8080/api/export
http://localhost:8080/api/export?format=text
http://localhost:8080/api/export?format=word
```

After Export, manually review and save the files in `processed_review/`.

## Send

`GET /api/send` starts the send workflow after manual review. The optional `format` parameter defaults to `text`.

```text
http://localhost:8080/api/send
http://localhost:8080/api/send?format=text
http://localhost:8080/api/send?format=word
```
