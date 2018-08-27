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
	  "name": "nvidia 2080",
	  "sources": [
	    {
	      "sourceName": "XKOM",
	      "path": "/p/445397-karta-graficzna-nvidia-asus-geforce-rtx-2080-dual-oc-8gb-gddr6.html"
	    },
	    {
	      "sourceName": "KOMPUTRONIK",
	      "path": "/product/533454/asus-geforce-rtx-2080-dual-oc-8gb.html"
	    },
	    {
	      "sourceName": "MORELE",
	      "path": "/karta-graficzna-msi-geforce-rtx-2080-gaming-x-trio-oc-rtx-2080-gaming-x-trio-oc-4141833/"
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
    },
    {
      "name": "wd hdd",
      "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/156834-dysk-hdd-wd-4tb-intellipower-64mb-red.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/199732/wd-red-4tb.html"
        },
        {
          "sourceName": "MORELE",
          "path": "/dysk-western-digital-red-3-5-4tb-sata-600-64mb-cache-wd40efrx-606541/"
        }
      ]
    },
    {
      "name": "power supply",
      "sources": [
        {
          "sourceName": "MORELE",
          "path": "/zasilacz-evga-supernova-g3-650w-220-g3-0650-y1-1103093/"
        }
      ]
    },
    {
      "name": "liquid metal",
      "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/396352-pasta-termoprzewodzaca-thermal-grizzly-liquid-metal-1g.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/307630/thermal-grizzly-pasta-termoprzewodzaca-conductonaut-1-g-0-16-ml-plynny-metal.html"
        }
      ]
    }
  ],
  "task": [
    {
      "name": "nvidia 2080"
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
    },
    {
      "name": "wd hdd"
    },
    {
      "name": "power supply"
    },
    {
      "name": "liquid metal"
    }
  ]
}'
```
