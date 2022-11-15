using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class FurniturePanel : MonoBehaviour
{
    public RectTransform contents;
    public GameObject furnitureItem;
    private long posId;
    private ItemData usedItem; 
    private ArrayList ownedItemList;
    private ArrayList lockedItemList;
    private bool isStarted = false;
    public static int idx;

    void Start()
    {
        isStarted = true;
    }

    private void OnEnable()
    {
        if (isStarted && DataManager.Instance)
        {
            idx = 0;
            usedItem = null;
            ownedItemList = new ArrayList();
            lockedItemList = new ArrayList();

            // �ε�� ������ ������
            var totalItemData = DataManager.Instance.totalItemDicData[posId];
            RawData[] filteredUserItemData; // �ش� ��ġ�� �ִ� user item list (UserItemData type)

            if (DataManager.Instance.userItemDicData.ContainsKey(posId))
            {
                filteredUserItemData = DataManager.Instance.userItemDicData[posId];
            } else
            {
                filteredUserItemData = new RawData[] { };
            }


            // 0, 1, 2 
            // 0: ����X 
            // 1: ����O, ���X 
            // 2: ����O, ���O

            // ������ ������ 
            for (var i=0; i<totalItemData.Length; i++) // �ش� ��ġ�� item �� ���鼭 
            {
                var data = (ItemData)totalItemData[i];                
                int hasItemCode = 0; 

                for (var j = 0; j < filteredUserItemData.Length; j++) // ������ �ִ��� Ȯ��
                {
                    if(data.id == filteredUserItemData[j].id) // ������ �ִ� ��� 
                    {
                        hasItemCode = ((UserItemData)filteredUserItemData[j]).hasItemCode;
                        break; 
                    }
                }

                if (hasItemCode == 0) // ����X 
                {
                    lockedItemList.Add(data);
                } else if(hasItemCode == 1) // ����O, ���X 
                {
                    ownedItemList.Add(data);
                } else // hasItemCode == 2 // ����O, ���O
                {
                    usedItem = data;
                }
            }

            GameObject listItem;
            FurnitureItem furnitureItem; 

            // ���������� ���� ������Ʈ ������ ���� 
            // ������� ���� ������
            if (usedItem != null)
            {
                listItem = Instantiate(this.furnitureItem, this.contents);
                furnitureItem = listItem.GetComponent<FurnitureItem>();
                furnitureItem.Init(usedItem.id, posId, usedItem.img, usedItem.name, usedItem.price, 2);
            }

            // �������� ���� ������ 
            for(var i=0; i<ownedItemList.Count; i++)
            {
                listItem = Instantiate(this.furnitureItem, this.contents);
                furnitureItem = listItem.GetComponent<FurnitureItem>();
                var ownedItem = (ItemData)ownedItemList[i];
                furnitureItem.Init(ownedItem.id, posId, ownedItem.img, ownedItem.name, ownedItem.price, 1);
            }

            // ����ִ� ���� ������ 
            for (var i = 0; i < lockedItemList.Count; i++)
            {
                listItem = Instantiate(this.furnitureItem, this.contents);
                furnitureItem = listItem.GetComponent<FurnitureItem>();
                var lockedItem = (ItemData)lockedItemList[i];
                furnitureItem.Init(lockedItem.id, posId, lockedItem.img, lockedItem.name, lockedItem.price, 0);
            }



            /*var listItem = Instantiate(this.shopItem, this.contents);*/ // ���� ������Ʈ�� ������ ���� 
                                                                          // GameObject UnityEngine.Object.Instantiate<GameObject>(GameObject original, Transform parent)
                                                                          // Ư�� ���̾��Ű ��ġ���� �����ϱ� ���� ����� �� �ִ�.
                                                                          // �ְ� ���� ������Ʈ�� �� ��° �Ķ������ parent�� �����ָ� ���� ������ ���� �ڽ����� �����ȴ�. 
            /*var shopListItem = listItem.GetComponent<ShopListItem>();*/
            /*var itemData = (ItemData)DataManager.Instance.itemIdToItem(id, data.id);

            shopListItem.Init(itemData.id, itemData.img, itemData.price, id, true);*/
        }
    }

    private void OnDisable()
    {
        Transform[] childlist = contents.gameObject.GetComponentsInChildren<Transform>();
        if (childlist != null)
        {
            for (int i = 1; i < childlist.Length; i++)
            {
                if (childlist[i] != transform)
                    Destroy(childlist[i].gameObject);
            }
        }
    }

    public void SetPosId(long posId)
    {
        this.posId = posId;
    }
}