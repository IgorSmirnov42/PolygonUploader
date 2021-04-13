# PolygonUploader
CLI tool for uploading competitive programming problems to Polygon

# Setup

* Create empty problem on Polygon
* Set environmental variables `polygon_key` and `polygon_secret` with API key and secret
* Prepare problem on your computer. Directories should have the following hierarchy:

```
root problem directory
│   check.cpp
│   file001.txt    
│
└───tests
│   └───tests
│   │   │   001
│   │   │   002
│   │   │   ...
│   │
│   └───...
│  
└───src
│   │   validate.cpp
│
└───solutions
│   │
│   │   [solution-name].cpp
│   │   ...
│
└───statement
│   │
│   │   [problem-name].tex
```
# Run

## Linux

`./polyloader.sh PROBLEM_NAME PATH_TO_PROBLEM_ROOT_DIRECTORY [POLYGON_API_URL]`

## Windows (not tested)

`gradlew.bat run --args="PROBLEM_NAME PATH_TO_PROBLEM_ROOT_DIRECTORY [POLYGON_API_URL]"`