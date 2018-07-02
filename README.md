# pretium
cost planner/optimizer for ordering stuff

## example requests
```
curl -X POST \
  http://localhost:8070/prices \
  -H 'content-type: application/json' \
  -d '{ 
  "items": [
    {
      "name": "intel i7 cpu",
      "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/383508-procesor-intel-core-i7-intel-i7-8700k-370ghz-12mb.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/421796/intel-core-i7-8700k.html"
        },
        {
          "sourceName": "MORELE",
          "path": "/procesor-intel-core-i7-8700k-3-70ghz-12mb-box-bx80684i78700k-978219/"
        }
      ]
	},
	{
	  "name": "nvidia 1080 ti",
	  "sources": [
	    {
	      "sourceName": "XKOM",
	      "path": "/p/400031-karta-graficzna-nvidia-evga-geforce-gtx-1080-ti-sc-black-edition-11gb-gddr5x.html"
	    },
	    {
	      "sourceName": "KOMPUTRONIK",
	      "path": "/product/386831/evga-geforce-gtx-1080-ti-sc-black-edition-gaming-11gb.html"
	    },
	    {
	      "sourceName": "MORELE",
	      "path": "/karta-graficzna-evga-geforce-gtx-1080-ti-sc2-gaming-11gb-gddr5x-352-bit-dvi-hdmi-3x-dp-11g-p4-6593-kr-1235745/"
	    }

	  ]
	}
  ],
  "task": [
    {
      "name": "nvidia 1080 ti"
    },
    {
      "name": "intel i7 cpu"
    }
  ]
}'
```
