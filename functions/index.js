const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendVisitorNotification = functions.firestore
  .document("new_visitor/{visitorId}")
  .onCreate(async (snap, context) => {
    const visitorData = snap.data();
    const visitorName = visitorData.visitor_name;
    const visitReason = visitorData.reason;
    const personId = visitorData.person_id.toString();
    const visitorDocumentId = snap.id;
    const visitorType = visitorData.person_name.includes("Student")
      ? "Students"
      : "Faculty";

    if (!personId) {
      console.log("No person_id found for this visitor entry.");
      return null;
    }

    const tokenDoc = await admin
      .firestore()
      .collection(visitorType)
      .doc(personId)
      .get();

    if (!tokenDoc.exists) {
      console.log(`No document found for personId: ${personId} in ${visitorType}`);
      return null;
    }

    const fcmToken = tokenDoc.data().fcm_token;

    if (!fcmToken) {
      console.log(`FCM token for personId: ${personId} is empty.`);
      return null;
    }

    const message = {
      notification: {
        title: `New Visitor: ${visitorName}`,
        body: `Reason: ${visitReason}`,
      },
      data: {
        visitor_name: visitorName,
        visit_reason: visitReason,
        document_id: visitorDocumentId,
        person_id: personId,
      },
      token: fcmToken,
      android: {
        priority: "high",
        notification: {
          sound: "default",
          channelId: "visitor_notifications",
          priority: "high",
        },
      },
    };

    try {
      const response = await admin.messaging().send(message);
      console.log("Successfully sent message:", response);
    } catch (error) {
      console.error("Error sending message:", error);
      if (
        error.code === "messaging/invalid-argument" ||
        error.code === "messaging/registration-token-not-registered"
      ) {
        console.log(`Invalid or expired token for ${personId}. Deleting.`);
        await admin
          .firestore()
          .collection(visitorType)
          .doc(personId)
          .update({ fcm_token: null });
      }
    }

    return null;
  });