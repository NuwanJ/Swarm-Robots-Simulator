package communication;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nadun
 */
public enum MessageType {
    
    FollowMe, GoAway, ComeCloser,
    Disperse, DisperseAck,
    PositionInfo, PositionInfoAck,
    ColorExchange, Join,
    Pulse, PulseFeedback, Leave,
    Update, Info, JoinPattern,
    JoinPatternRequest,JoinPatternResponse, 
    PositionDataReq,PositionAcquired,
    PositionData, Command
    
}
