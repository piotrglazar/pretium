# pretium
cost planner/optimizer for ordering stuff

## example requests
```
curl -X POST \
  http://localhost:8070/prices \
  -H 'content-type: application/json' \
  -d '{
	"name": "intel i7 cpu",
	"sources": [
		{
			"sourceName": "XKOM",
			"path": "/p/383508-procesor-intel-core-i7-intel-i7-8700k-370ghz-12mb.html"
		},
        {
            "sourceName": "KOMPUTRONIK",
            "path": "/product/421796/intel-core-i7-8700k.html"
        }
	]
}'
```
