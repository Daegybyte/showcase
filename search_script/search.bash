#!/bin/bash

# Parse command line options
while getopts ":a" opt; do
  case "${opt}" in
    a)
      all_sites=true
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
  esac
done
shift $((OPTIND -1))

# Get the search query from the command line arguments
query="$@"

# Construct the search URL
if [ "$all_sites" = true ]; then
  url="https://www.google.com/search?q=$query"
else
  
url="https://www.google.com/search?q=$query+site:github.com+OR+site:w3schools.com+OR+site:stackoverflow.com+OR+site:medium.com+OR+site:reddit.com+OR+site:youtube.com+OR+site:geeksforgeeks.org"
fi

# Open the search URL in the default browser
open "$url"
