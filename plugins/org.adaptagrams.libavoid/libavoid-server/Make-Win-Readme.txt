Building the server binary for windows is tested with Microsoft Visual Studio 2010 SP1

1. Make sure the libavoid library is compiled to a static library for both x86 and x64.
	Libavoid project settings - General - Configuration Type - Static Library (.lib)


2. In the libavoid-server properties, adapt following paths
	C/C++ - General - Additional Include Directories
	Linker - General - Additional Library Directories