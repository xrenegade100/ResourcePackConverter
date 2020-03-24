# Resource Pack Converter

This is our 1.8-1.12 -> 1.13/1.14/1.15 Resource pack converter.

We know that many use resource packs in nonstandard and quirky ways - but giving this a shot *may* reduce quite a bit of your pain and workload for the 1.13 conversion.

This should convert most things, but if it doesn't please let me know what didn't work so I can fix it.

Also if any other developers would like to open any PRs with fixes and additions please feel free.

While this program will copy your resource packs before converting them, we still recommend backing them up, just in case!

## Usage
[Download the compiled jar file](https://github.com/xrenegade100/ResourcePackConverter/releases/latest), or compile the source yourself.  
The program will look for any valid resource packs in the current directory and is easily run by doing this.
(Converts from 1.12.x to 1.15.x) :blush:

    java -jar ResourcePackConverter.jar

To display detailed information about model conversion (which can fail in case of malformed JSONs files) to inspect what files are being processed during the conversion you can use the `--model-verbose` parameter.

    java -jar ResourcePackConverter.jar --model-verbose

You can set the input directory using one of the following parameters.
`-i <path>`, `--input <path>` or `--input-dir <path>`.

    java -jar ResourcePackConverter.jar --input input/
	
To update to a newer version than 1.13, you can use these parameters.
`--from <version>` and `--to <version>`

	java -jar ResourcePackConverter.jar --from 1.12 --to 1.15

If you are converting a resource pack to version 1.15 you can use the parameter `--light <front|side>` to choose how are items displayed in the new GUI system.

    java -jar ResourcePackConverter.jar --light front

We hope this helps out!
