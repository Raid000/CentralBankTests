package org.example;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Assertions;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;

public class TestPacketEPD {

    private static final Logger LOGGER = Logger.getLogger(TestPacketEPD.class.getName());
    private static Element packetEPDHeader;
    private static Element ed807Header;
    private static List<Element> ed101Docs;


    @BeforeAll
    static void setUp() throws Exception{
        SAXBuilder builder = new SAXBuilder();
        String packetEDPpath = "C:\\Users\\azatg\\IdeaProjects\\headHunterTest\\PacketEPD.xml";
        Document packetEPD = builder.build(new File(packetEDPpath));
        String ed807Path = "C:\\Users\\azatg\\IdeaProjects\\headHunterTest\\20231026_ED807_full.xml";
        Document ed807 = builder.build(new File(ed807Path));
        packetEPDHeader = packetEPD.getRootElement();
        ed807Header = ed807.getRootElement();
        ed101Docs = packetEPDHeader.getChildren("ED101");
    }

    @Test
    void testPacketEPDRequiredRequisites(){
        /* Обязательные реквизиты заголовка пакета (PacketEPD) EDNo, EDDate и EDAuthor
         соответствуют значениям этих же реквизитов заголовка пакета ED807".
         Требование 2.2.1.*/

        Assertions.assertEquals(packetEPDHeader.getAttributeValue("EDNo"), ed807Header.getAttributeValue("EDNo"),
                "Реквизиты EDNo пакетов PacketEPD и ED807 не соответствуют друг другу");
        Assertions.assertEquals(packetEPDHeader.getAttributeValue("EDDate"), ed807Header.getAttributeValue("EDDate"),
                "Реквизиты EDDate пакетов PacketEPD и ED807 не соответствуют друг другу");
        Assertions.assertEquals(packetEPDHeader.getAttributeValue("EDAuthor"), ed807Header.getAttributeValue("EDAuthor"),
                "Реквизиты EDAuthor пакетов PacketEPD и ED807 не соответствуют друг другу");
        LOGGER.log(Level.INFO,"Обязательные реквизиты заголовка пакета (PacketEPD) EDNo, EDDate и EDAuthor" +
                " соответствуют значениям этих же реквизитов заголовка пакета ED807");
    }

    @Test
    void testDocQuantityAndSum(){
        /* Проверка подсчёта общего количества документов (EDQuantity) и суммы всех входящих в пакет документов (Sum)
        Требование 2.2.1.*/

        int ed101Count = ed101Docs.size();
        int totalSum = 0;
        for (Element ed101 : ed101Docs) {
            String sumString = ed101.getAttributeValue("Sum");
            if (sumString != null) {
                totalSum += Integer.parseInt(sumString);
            }
        }
        Assertions.assertEquals(Integer.parseInt(packetEPDHeader.getAttributeValue("Sum")), totalSum,
                "Значение атрибута Sum не соответствует сумме всех входящих в пакет документов");
        Assertions.assertEquals(Integer.parseInt(packetEPDHeader.getAttributeValue("EDQuantity")), ed101Count,
                "Значение атрибута EDQuantity не соответствует общему количеству документов в пакете");
        LOGGER.log(Level.INFO,"Значение атрибута Sum соответствует общему количество документов" +
                " и EDQuantity соответствуют сумме всех входящих в пакет документов");
    }

    @Test
    void testSystemCodeValue(){
        /* Атрибут SystemCode соответствует значению "01"
        Требование 2.2.1.*/

        Assertions.assertEquals("01", packetEPDHeader.getAttributeValue("SystemCode"),
                "Атрибут SystemCode не соответствует значению \"01\" согласно требованию 2.2.1.");
        LOGGER.log(Level.INFO, "Атрибут SystemCode соответствует значению \"01\" согласно требованию 2.2.1.");
    }

    @Test
    void testSameAuthorAndDate(){
        /* EDDate и EDAuthor равны значениям заголовка пакета PacketEPD
        Требование 2.2.2.*/

        for (Element ed101 : ed101Docs) {
            Assertions.assertEquals(packetEPDHeader.getAttributeValue("EDDate"), ed101.getAttributeValue("EDDate"),
                    "Атрибут EDDate документа ED101 не соответствует значению атрибута EDDate пакета PacketEPD");
            Assertions.assertEquals(packetEPDHeader.getAttributeValue("EDAuthor"), ed101.getAttributeValue("EDAuthor"),
                    "Атрибут EDAuthor документа ED101 не соответствует значению атрибута EDAuthor пакета PacketEPD");
        }
        LOGGER.log(Level.INFO, "Атрибуты EDDate и EDAuthor документов ED101 эквивалентны соответствующим атрибутам пакета PacketEPD" +
                "согласно требованию 2.2.2.");
    }

    @Test
    void testEDNoIncremented(){
        /* EDNo начинается с 1 и инкрементируется на +1 в каждом следующем ED101
        Требование 2.2.2.*/

        int i = 1;
        Assertions.assertEquals(i, Integer.parseInt(ed101Docs.get(0).getAttributeValue("EDNo")),
                "EDNo должна начинаться со значения 1, согласно требованию 2.2.2.");
        for (Element ed101 : ed101Docs) {
            Assertions.assertEquals(i++, Integer.parseInt(ed101.getAttributeValue("EDNo")),
                    "EDNo должна инкрементироваться на +1 в каждом следующем ED101, согласно требованию 2.2.2.");
        }
        LOGGER.log(Level.INFO, "EDNo успешно инкрементируется на +1");
    }
}