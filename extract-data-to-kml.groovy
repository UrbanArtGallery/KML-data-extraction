@Grapes(
    @Grab(group='org.jsoup', module='jsoup', version='1.8.2')
)

import org.jsoup.Jsoup
import groovy.xml.XmlUtil

File dataFile = new File("doc.kml")
def parser = new XmlParser()

Node data = parser.parseText(dataFile.text)

// Helper function to pull the relevant text from the table row
def getValue = { row ->
    return row.select('td').last().text()
}

def placemarks = data.Document.Folder.Placemark

// For each of the Descriptions, replace the HTML tabular data with XML
placemarks.each { placemark ->

    def descriptionNode = Jsoup.parse(placemark.description.text())
    def rows = descriptionNode.select('table').last().select('tr')
    def point = placemark.Point.coordinates.text().split(',')

    placemark.description.replaceNode {
        description() {
            artist(getValue(rows[0]))
            title(getValue(rows[1]))
            date(getValue(rows[2]))
            materials(getValue(rows[3]))
            address(getValue(rows[4]))
            description(getValue(rows[5]))
            type(getValue(rows[6]))
            url1(rows[7].select('td').last().select('a').attr('href'))
            url2(rows[8].select('td').last().select('a').attr('href'))
            location() {
                lat(point[1].trim())
                'long'(point[0].trim())
            }
        }
    }

}

// Write out the XML data
new File('transformed-data.kml').write(XmlUtil.serialize(data))