package ithaic.imitate_os.fileManager.fileKind;

import lombok.Data;

@Data
public class CatalogItem{
    private char[] CatalogItem = new char[8];//目录项内容
    public CatalogItem(char[] name, char extendedName, char property, char startBlock, char length){
        createCatalogItem(name, extendedName, property, startBlock, length);
    }
    /**
     * 封装目录项
     * @param name:         文件名
     * @param extendedName: 扩展名
     * @param property:     文件属性
     * @param freeBlock:    起始盘块号
     * @param fileLength:   文件大小，单位为盘块数
     */
    private void createCatalogItem(char[] name, char extendedName, char property, char freeBlock, char fileLength){
        for (int i = 0; i < 8; i++) {
            CatalogItem[i] = (char) 0;
        }
        for (int i = 0; i < name.length; i++) {
            CatalogItem[i] = name[i];
        }
        CatalogItem[3] = extendedName;
        CatalogItem[4] = property;
        CatalogItem[5] = freeBlock;
        CatalogItem[6] = (char) 0;
        CatalogItem[7] = fileLength;
    }
}