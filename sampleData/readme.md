```bash
for i in (cat firstEventBee.txt | jq -c '.[]')
	curl --header "Content-Type: application/json" --request PUT --data $i http://localhost:8888/bee
end
```
