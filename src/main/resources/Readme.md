## Script.sh Functions Overview

### readConfiguration
- Reads configuration.json file and extract arguments related to a job, Gasw configurations and constants.

### info
- Logs an informational message with the current date and time.

### warning
- Logs a warning message with the current date and time.

### error
- Logs an error message with the current date and time and redirects it to the standard error stream.

### startLog
- Starts a log section by printing a message enclosed in angle brackets to both standard output and error streams.

### stopLog
- Ends a log section by printing a closing message enclosed in angle brackets to both standard output and error streams.

### cleanup
- Performs cleanup tasks such as unmounting directories, killing background processes, and printing log information. It's executed when the script exits.

### checkCacheDownloadAndCacheLFN
- Checks if a file is available in the local cache. If not, it downloads the file and adds it to the cache.

### refresh_token
- Refreshes an authentication token periodically using a background process. Used for accessing secure services.

### stopRefreshingToken
- Stops the background token refreshing process.

### wait_for_token
- Waits for the authentication token to be refreshed and becomes available.

### downloadLFN
- Downloads a file from a logical file name (LFN) path using the DIRAC command.

### downloadGirderFile
- Downloads a file from a Girder server using the Girder client.

### mountGfal
- Mounts a directory using gfalFS for accessing grid storage elements.

### unmountGfal
- Unmounts all gfalFS-mounted directories.

### downloadShanoirFile
- Downloads a file from the Shanoir platform using an API URL and authentication token.

### downloadURI
- Downloads a file from various URI schemes including LFN, file, HTTP, Girder, Shanoir, and SRM.

### validateDownload
- Validates if the download was successful and exits with an error message if not.

### addToCache
- Adds a downloaded file to the local cache with its timestamp.

### addToFailOver
- Adds a file to a failover storage element for redundancy.

### nSEs
- Counts the number of storage elements in the list.

### getAndRemoveSE
- Gets and removes a storage element from the list by its index.

### chooseRandomSE
- Chooses a random storage element from the list.

### uploadLfnFile
- Uploads a file to a logical file name (LFN) path using the DIRAC command.

### uploadShanoirFile
- Uploads a file to the Shanoir platform using an API URL and authentication token.

### uploadGirderFile
- Uploads a file to a Girder server using the Girder client.

### upload
- Uploads a file to various URI schemes including LFN, file, Shanoir, and Girder.

### delete
- Deletes a file from various URI schemes including LFN, file, and Girder.

### download_udocker
- Installs udocker and sets up the necessary environment for running Docker containers without root privileges.

### checkBosh
- Checks for the availability of the `bosh` executable and installs it if necessary.

### boutiques_exec
- Executes a Boutiques tool using `bosh` and captures the execution provenance.

### provenance
- Processes the provenance information to extract output file names.

### copyProvenanceFile
- Copies the provenance file to a specified destination.

### createOutputDir
- Creates a directory for uploading outputs based on the given upload path.
