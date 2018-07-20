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
	},
	{
	  "name": "samsung ssd",
	  "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/431142-dysk-ssd-samsung-500gb-970-evo-m2-2280-nvme.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/488109/samsung-970-evo-m-2-500gb.html"
        }
      ]
	},
	{
      "name": "ram gskill",
      "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/266766-pamiec-ram-ddr4-gskill-32gb-3000mhz-ripjaws-v-red-cl15-2x16gb.html"
        }
      ]
    },
    {
      "name": "ram kingston",
      "sources": [
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/323141/hyperx-predator-xmp-64gb-4x16gb-3000mhz-ddr4-cl15-dimm-.html"
        }
      ]
    },
    {
      "name": "motherboard",
      "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/387339-plyta-glowna-socket-1151-asus-rog-maximus-x-hero-wi-fi-ac.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/433281/asus-rog-maximus-x-hero-wifi-ac.html"
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
    },
    {
      "name": "samsung ssd"
    },
    {
      "items": [
        {
          "name": "ram gskill",
          "quantity": 2
        },
        {
          "name": "ram kingston"
        }
      ]
    },
    {
      "name": "motherboard"
    }
  ]
}'
```
