def root = new XmlSlurper().parse("wordpress.xml")

root.channel.item.each {
	if (it.post_type == "post") {
		def content = it.encoded.text()
			.replaceAll(/<[\/]*strong>/, "*")
			.replaceAll(/<[\/]*em>/, "*")
			.replaceAll(/<a.*href="(.*)".*>(.*)<\/a>/) { all, href, title ->
				"[${title}](${href})  "
			}
		def dateString = it.post_date.text().replaceAll(/([\d]{4}-\d{2}-\d{2}) .*/) { all, date ->	"${date}" }
		def yearString = dateString.replaceAll(/([\d]{4}).*/) {all, year -> "${year}"}

		def folder = new File("../content/review/${yearString}")
		if (!folder.exists() ) {
			folder.mkdirs()
		}
		new File(folder, dateString + "-" + it.post_name.text() + ".md").withWriter("utf-8") { writer ->
			writer.writeLine "+++"
			writer.writeLine "title = \"" + it.title.text() + "\""
			writer.writeLine "date = \"" + it.post_date + "\""

			writer.writeLine "slug =\"" + it.post_name + "\""

			writer.write "artist = ["
			writer.write it.category.findAll { it."@domain" == "artist" }
				.collect { "\"" + it + "\""}
				.join(', ')
			writer.writeLine "]"

			writer.write "category = ["
			writer.write it.category.findAll { it."@domain" == "category" }
				.collect { "\"" + it + "\""}
				.join(', ')
			writer.writeLine "]"

			writer.write "label = ["
			writer.write it.category.findAll { it."@domain" == "label" }
				.collect { "\"" + it + "\""}
				.join(', ')
			writer.writeLine "]"

			writer.write "genre = ["
			writer.write it.category.findAll { it."@domain" == "genre" }
				.collect { "\"" + it + "\""}
				.join(', ')
			writer.writeLine "]"

			def player = it.postmeta.find { it.meta_key == "review_embedded_player" } .meta_value.text().replaceAll(/"/, "'")
			if (player != "") {
				writer.writeLine ""
				writer.writeLine "player = \"" + player + "\""
				writer.writeLine ""
			}

			def thumbid = it.postmeta.find { it.meta_key == "_thumbnail_id" } .meta_value.text() + ""
			if (thumbid != "") {
				def thumb = root.channel.item.find { it.post_id == thumbid }.postmeta.find { it.meta_key == "_wp_attached_file"}.meta_value.text() + ""
				writer.writeLine "image = \"" + thumb + "\""
			}

			writer.writeLine "+++"
			writer.writeLine ""

			writer.writeLine "" + content + ""

		}
	}
}
