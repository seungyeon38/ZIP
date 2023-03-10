using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Networking;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using Newtonsoft.Json;
using Photon.Pun;
using Photon.Realtime;
using TMPro;
using Photon.Voice.Unity;
using Photon.Voice.PUN;

public class LoadingSceneManager : MonoBehaviourPunCallbacks
{
    #region  Public Fields
    public Slider slider;
    public string sceneName;
    #endregion
    #region Private Fields
    private DataManager dataManager;
    private float gage = 10000;
    private float doneGage = 0;
    private float tempGage = 0;
    private int coroutineNum = 0;
    private string gameVersion = "1";
    [SerializeField]
    private TMP_Text loadingText;
    [SerializeField]
    private TMP_Text taskText;
    private string task;
    [SerializeField]
    private TMP_Text tip;
    #endregion

    #region MonoBehaviour Callbacks
    void Awake()
    {
        dataManager = DataManager.Instance;
    }
    // Start is called before the first frame update
#if UNITY_EDITOR
    void Start()
    {
        StartCoroutine(StartLoading());
    }
#endif
    void Update()
    {
        taskText.text = task + " (" + ((int)(doneGage / gage * 100)) + "%)";
    }
    #endregion

    #region MonoBehaviourPunCallbacks Callbacks;
    public override void OnConnectedToMaster()
    {
        /*Debug.Log("PUN Basics Tutorial/Launcher: OnConnectedToMaster() was called by PUN");
        Debug.Log("join room");*/
        /*PhotonNetwork.JoinOrCreateRoom(dataManager.user.family.id.ToString(), new RoomOptions { MaxPlayers = (byte)dataManager.user.family.memberNum }, TypedLobby.Default);*/
        PhotonNetwork.JoinRoom(dataManager.user.family.id.ToString());
    }
    public override void OnDisconnected(DisconnectCause cause)
    {
        /*Debug.LogWarningFormat("PUN Basics Tutorial/Launcher: OnDisconnected() was called by PUN with reason {0}", cause);*/
    }
    public override void OnJoinRoomFailed(short returnCode, string message)
    {
        Dictionary<long, RawData[]> furnitureData = DataManager.Instance.userItemDicData;
        ExitGames.Client.Photon.Hashtable setValue = new ExitGames.Client.Photon.Hashtable();
        ExitGames.Client.Photon.Hashtable furnitureSet = new ExitGames.Client.Photon.Hashtable();
        foreach (long positionId in furnitureData.Keys)
        {
            foreach (RawData data in furnitureData[positionId])
            {
                if (((UserItemData)data).hasItemCode == 2)
                {
                    if (furnitureSet.ContainsKey(positionId))
                    {
                        furnitureSet.Remove(positionId);
                    }
                    furnitureSet.Add(positionId, data.id);
                    break;
                }
            }
        }
        setValue.Add("furniture", furnitureSet);

        //?????? ?????? ??????
        ExitGames.Client.Photon.Hashtable photoSet = new ExitGames.Client.Photon.Hashtable();
        Dictionary<long, RawData> photoData = DataManager.Instance.userAlbumDicData;
        foreach (long albumId in photoData.Keys)
        {
            if (((UserAlbumData)photoData[albumId]).texture != null)
            {
                if (photoSet.ContainsKey(albumId))
                {
                    photoSet.Remove(albumId);
                }
                photoSet.Add(albumId, JsonUtility.ToJson((UserAlbumData)photoData[albumId]));
            }
        }
        setValue.Add("photo", photoSet);
        setValue.Add("action", "init");
        //photon??? ??????
        /*PhotonNetwork.CurrentRoom.SetCustomProperties(setValue);*/
        PhotonNetwork.CreateRoom(dataManager.user.family.id.ToString(), new RoomOptions { MaxPlayers = (byte)dataManager.user.family.memberNum, CustomRoomProperties = setValue });
    }

    public override void OnJoinedRoom()
    {
        /*Debug.LogFormat("PUN Basics Tutorial/Launcher: OnJoinedRoom() called by PUN. Now this client is in a room. room name {0}", PhotonNetwork.CurrentRoom.Name);*/
        doneGage += 10f;

    }
    public override void OnCreatedRoom()
    {
        //?????? ?????? ??????

    }
    /*public override void OnCreatedRoom()
    {
        Dictionary<long, RawData[]> arrData = DataManager.Instance.userItemDicData;
        ExitGames.Client.Photon.Hashtable setValue = new ExitGames.Client.Photon.Hashtable();
        ExitGames.Client.Photon.Hashtable subSet = new ExitGames.Client.Photon.Hashtable();
        foreach (long positionId in arrData.Keys)
        {
            foreach (RawData data in arrData[positionId])
            {
                bool temp = false;
                Debug.Log("position" + positionId + " data " + data.id + " has " + ((UserItemData)data).hasItemCode);
                if (((UserItemData)data).hasItemCode == 2)
                {
                    temp = true;
                    if (setValue.ContainsKey("position" + positionId))
                    {
                        setValue.Remove("position" + positionId);
                    }
                    setValue.Add("position" + positionId, data.id);
                    Debug.Log(setValue["position" + positionId]);
                    break;
                }
            }
        }
        PhotonNetwork.CurrentRoom.SetCustomProperties(setValue);
    }*/
    #endregion

    public void Init(string token)
    {
        APIManager.SetAccessToken(token);
        StartCoroutine(StartLoading());
    }
    public void InitHome(string token)
    {
        APIManager.SetAccessToken(token);
        StartCoroutine(StartHome());
    }
    #region Private Methods
    private void Connect()
    {
        task = "?????? ????????? ???????????? ???";
        PhotonNetwork.NickName = DataManager.Instance.user.nickName;
        if (PhotonNetwork.IsConnected)
        {
            PhotonNetwork.JoinRoom(dataManager.user.family.id.ToString());
        }
        else
        {
            PhotonNetwork.GameVersion = gameVersion;
            PhotonNetwork.ConnectUsingSettings();
        }
    }

    private IEnumerator StartLoading()
    {
        slider.transform.gameObject.SetActive(true);
        StartCoroutine(UpdateLoadingText());
        StartCoroutine(LoadAsynSceneCoroutine(sceneName));
        yield return StartCoroutine(LoadAlbumData());
        yield return StartCoroutine(LoadUserInfo());
        task = "?????? ?????? ???????????? ???";
        yield return StartCoroutine(dataManager.LoadUserItemData());
        yield return StartCoroutine(LoadUserAlbumData());
        Connect();
    }
    private IEnumerator StartHome()
    {
        GameObject dm = GameObject.Find("DataManager");
        dm.transform.GetComponent<Recorder>().DestroyUniversal();
        dm.transform.GetComponent<PunVoiceClient>().DestroyUniversal();
        Destroy(GameObject.Find("VoiceLogger"));
        Destroy(GameObject.Find("PhotonMono"));
        /*SceneManager.LoadScene("Home");*/
        yield return null;
        StartCoroutine(UpdateLoadingText());
        StartCoroutine(LoadAsynSceneCoroutine("Home"));
        /*yield return StartCoroutine(LoadAlbumData());*/
        yield return StartCoroutine(LoadUserInfo());
        task = "?????? ?????? ???????????? ???";
        yield return StartCoroutine(dataManager.LoadUserItemData());
        yield return StartCoroutine(LoadUserAlbumData());
        gage = doneGage;
    }
    private IEnumerator LoadAsynSceneCoroutine(string scene)
    {
        AsyncOperation operation = SceneManager.LoadSceneAsync(scene); // scene??? ?????????????????? ?????? ?????? 
        operation.allowSceneActivation = false;  // ????????? ??????????????? ?????? ?????? ???????????? 

        while (!operation.isDone) // ?????? ??????
        {
            slider.value = doneGage / gage;
            if (doneGage == gage)
            {
                operation.allowSceneActivation = true; // ????????? ?????????????????? ?????? ????????? 
            }

            yield return null;
        }
    }
    private IEnumerator LoadUserInfo()
    {
        task = "????????? ?????? ???????????? ???";
        var json = "";
        using UnityWebRequest www = APIManager.GetWWW("GET", "/users/profiles", null);
        yield return www.SendWebRequest();
        if (www.result != UnityWebRequest.Result.Success)
        {
            Debug.LogError(www.error);
        }
        else
        {
            json = www.downloadHandler.text;
        }
        dataManager.user = JsonConvert.DeserializeObject<UserInfo>(json);
        www.Dispose();
        yield return null;
    }
    private IEnumerator LoadAlbumData()
    {
        task = "?????? ???????????? ???";
        dataManager.albumDicData = new Dictionary<long, RawData>(); // album id : AlbumData
        using UnityWebRequest www = APIManager.GetWWW("GET", "/album", null);
        yield return www.SendWebRequest();
        if (www.result != UnityWebRequest.Result.Success)
        {
            Debug.LogError(www.error);
        }
        else
        {
            var json = www.downloadHandler.text; // api ???????????? json ???????????? 
            var arrData = JsonConvert.DeserializeObject<AlbumData[]>(json);
            for (int i = 0; i < arrData.Length; i++) // album ???
            {
                tempGage += arrData[i].pictures.Length;
            }
            gage = tempGage + 14;
            for (int i = 0; i < arrData.Length; i++) // album ???
            {
                for (int j = 0; j < arrData[i].pictures.Length; j++) // ?????? ????????? ?????? ??? 
                {
                    while (coroutineNum > 3)
                    {
                        yield return new WaitForSeconds(0.5f);
                    }
                    coroutineNum++;
                    StartCoroutine(GetTexture(arrData[i].pictures[j]));
                    /*for (int k = 0; k < 10000; k++)
                    {
                        doneGage += 0.0001f;
                    }
                    doneGage = Mathf.Round(doneGage);*/
                }
            }

            foreach (var data in arrData)
            {
                dataManager.albumDicData.Add(data.id, data);
            }
        }
        www.Dispose();
        yield return null;
    }

    private IEnumerator LoadUserAlbumData()
    {
        task = "?????? ?????? ???????????? ???";
        DataManager.Instance.userAlbumDicData = new Dictionary<long, RawData>(); // ?????? pos id : UserAlbumData
        using UnityWebRequest www = APIManager.GetWWW("GET", "/unity/album", null);
        yield return www.SendWebRequest(); // api ???????????? json ???????????? 
        var json = www.downloadHandler.text;
        var arrData = JsonConvert.DeserializeObject<UserAlbumData[]>(json);
        if (arrData.Length == 0) doneGage += 4;
        foreach (UserAlbumData data in arrData)
        {
            yield return StartCoroutine(GetTexture(data));
            doneGage += 4.0f / arrData.Length;
            DataManager.Instance.userAlbumDicData.Add(data.id, data);
        }
        doneGage = Mathf.Round(doneGage);
        www.Dispose();
        yield return null;
    }
    private IEnumerator GetTexture(PhotoData picture)
    {
        using UnityWebRequest www = UnityWebRequestTexture.GetTexture(picture.url);
        yield return www.SendWebRequest();

        yield return picture.texture = DownloadHandlerTexture.GetContent(www);
        doneGage += 1;
        coroutineNum--;
        www.Dispose();
        yield return null;
    }
    private IEnumerator GetTexture(UserAlbumData picture)
    {
        using UnityWebRequest www = UnityWebRequestTexture.GetTexture(picture.url);
        yield return www.SendWebRequest();

        yield return picture.texture = DownloadHandlerTexture.GetContent(www);
        www.Dispose();
        yield return null;
    }

    private IEnumerator UpdateLoadingText()
    {
        int tipNum = 0;
        while (true)
        {
            switch (tipNum)
            {
                case 0:
                    tip.text = "tip) ????????? ????????? ??????????????? ?????? ?????????????????????.";
                    break;
                case 1:
                    tip.text = "tip) ????????? ????????? ????????? ????????? ?????? ??? ?????????.";
                    break;
                case 2:
                    tip.text = "tip) ????????? ???????????? ????????? ?????????! ???????????? ???????????????.";
                    break;
                default:
                    tipNum = 0;
                    break;
            }
            tipNum++;
            tipNum %= 3;

            for (int i = 0; i < 5; i++)
            {
                loadingText.text = "Loading ?? . .";
                yield return new WaitForSeconds(0.2f);
                loadingText.text = "Loading . ?? .";
                yield return new WaitForSeconds(0.2f);
                loadingText.text = "Loading . . ??";
                yield return new WaitForSeconds(0.2f);
            }
        }
    }
    #endregion
}