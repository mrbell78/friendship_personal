package ngo.friendship.satellite.asynctask;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.asynctask.async.AsyncTask;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.error.ErrorCode;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.utility.Utility;

public class CommiunicationTaskWithoutProgressBar extends AsyncTask<Void, Void, String> {


    private OnCompleteListener completeListener;
    private Context context;
    private RequestData request;

    Message message = Message.obtain();
    Bundle bundle = new Bundle();

    public CommiunicationTaskWithoutProgressBar(Context context, RequestData request) {
        this.context = context;
        this.request = request;
        this.request.setContext(this.context);
        this.message = Message.obtain();
        this.bundle = new Bundle();
        this.bundle.putString(TaskKey.NAME, "COMMUNICATION_TASK");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void params) throws Exception {
        String url =App.getContext().getGateWayBasePath()+App.getContext().getApiPath(request.getRequestType());
        boolean isAlternativePath=false;
        int i=1;
        while(i<2){
            try {
                ResponseData responseData = request.send(url,context);
                Utility.checkUserActive(context, responseData);
                bundle.putSerializable(TaskKey.DATA0, responseData);
                i++;
            }
            catch (MhealthException se) {
                se.printStackTrace();
                if(!isAlternativePath && (se.getCode()==ErrorCode.HTTP_HOST_CONNECT_EXCEPTION
                        ||se.getCode()==ErrorCode.CLIENT_PROTOCOL_EXCEPTION
                        ||se.getCode()==ErrorCode.CONNECT_TIMEOUT_EXCEPTION
                        ||se.getCode()==ErrorCode.CONNECT_EXCEPTION
                        ||se.getCode()==ErrorCode.CLIENT_PROTOCOL_EXCEPTION
                        ||se.getCode()==ErrorCode.HTTP_STATUS)){
                    if(App.getContext().getAlternativeGateWayBasePath()!=null){
                        url=App.getContext().getAlternativeGateWayBasePath()+App.getContext().getApiPath(request.getRequestType());
                        isAlternativePath=true;
                        continue;
                    }
                }
                bundle.putSerializable(TaskKey.ERROR_MSG, se.getMessage());
                i++;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //endTask();
        message.setData(bundle);
        if(completeListener!=null){
            completeListener.onComplete(message);
        }
    }

    @Override
    protected void onBackgroundError(Exception e) {

    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
