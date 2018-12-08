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
          "path": "/p/455833-procesor-intel-core-i7-intel-i7-9700k-36-ghz-12mb-box.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/555039/intel-core-i7-9700k.html"
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
      "name": "samsung ssd 1tb",
      "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/431147-dysk-ssd-samsung-1tb-970-evo-m2-2280-nvme.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/491280/samsung-970-evo-m-2-1tb.html"
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
          "path": "/p/454851-plyta-glowna-socket-1151-asus-rog-maximus-xi-hero-wi-fi.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/554205/asus-rog-maximus-xi-hero-wi-fi-.html"
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
          "path": "/zasilacz-evga-supernova-g3-750w-220-g3-0750-x1-1103094/"
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
    },
    {
      "name": "cpu cooling",
      "sources": [
        {
          "sourceName": "XKOM",
          "path": "/p/379937-chlodzenie-wodne-nzxt-kraken-x62-v2.html"
        },
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/406220/nzxt-kraken-x62-v2-280mm-chlodzenie-wodne.html"
        },
        {
          "sourceName": "MORELE",
          "path": "/chlodzenie-wodne-nzxt-kraken-x62-v-2-rl-krx62-02-977446/"
        }
      ]
    },
    {
      "name": "case",
      "sources": [
        {
          "sourceName": "KOMPUTRONIK",
          "path": "/product/204972/nzxt-phantom-530-biala.html"
        },
        {
          "sourceName": "MORELE",
          "path": "/obudowa-nzxt-phantom-530-ca-ph530-w1-610359/"
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
      "name": "samsung ssd 1tb"
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
    },
    {
      "name": "cpu cooling"
    },
    {
      "name": "case"
    }
  ]
}'
```
