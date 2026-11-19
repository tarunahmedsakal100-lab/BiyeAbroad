const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- Unauthenticated tests ---
test("Unauthenticated user: cannot create user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc("anon").set({
      userId: "anon",
      displayName: "Anon",
      email: "anon@test.com",
      createdAt: new Date(),
    })
  );
});

// --- User Profile Tests ---
test("Authenticated user: can create own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@test.com" }).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Khan",
      email: "alice@test.com",
      age: 26,
      gender: "female",
      country: "United Kingdom",
      city: "London",
      countryOfOrigin: "Bangladesh",
      nationality: "British",
      religion: "Islam",
      education: "MSc in Data Science",
      profession: "Software Engineer",
      maritalStatus: "Never Married",
      height: "5'4\"",
      bio: "Looking for a kind and ambitious life partner.",
      verificationStatus: "unverified",
      membershipTier: "free",
      isSuspended: false,
      role: "user",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot create profile with mismatched userId (spoofing)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@test.com" }).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob Fake",
      email: "bob@test.com",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot update another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob Ahmed",
      email: "bob@test.com",
      createdAt: new Date(),
      isSuspended: false,
      role: "user",
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({
      displayName: "Hacked by Alice",
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({
      photoUrl: "https://res.cloudinary.com/v0c1td87/image/upload/v1/hack.jpg",
      photoPublicId: "hack",
    })
  );
});

test("Authenticated owner: can update own Cloudinary photoUrl and photoPublicId; Base64 rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@test.com" }).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Khan",
      email: "alice@test.com",
      role: "user",
      isSuspended: false,
      createdAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      photoUrl: "https://res.cloudinary.com/v0c1td87/image/upload/v1731880000/biyeabroad/alice.jpg",
      photoUrls: ["https://res.cloudinary.com/v0c1td87/image/upload/v1731880000/biyeabroad/alice.jpg"],
      photoPublicId: "biyeabroad/alice",
      updatedAt: new Date(),
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      photoUrl: "data:image/jpeg;base64,/9j/4AAQSkZJRg==",
      photoPublicId: "bad_base64",
    })
  );
});

// --- Interests Tests ---
test("Authenticated user: can send interest to another user", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("interests").doc("int_1").set({
      interestId: "int_1",
      senderId: ALICE_UID,
      receiverId: BOB_UID,
      status: "pending",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot send interest on behalf of someone else", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("interests").doc("int_2").set({
      interestId: "int_2",
      senderId: BOB_UID,
      receiverId: ALICE_UID,
      status: "pending",
      createdAt: new Date(),
    })
  );
});

// --- Conversations & Messages Tests ---
test("Non-member: cannot read conversations of other members", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("conversations").doc("conv_1").set({
      conversationId: "conv_1",
      members: [BOB_UID, "charlie_789"],
      lastMessage: "Hello",
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("conversations").doc("conv_1").get()
  );
});

test("Conversation member: can send message in their conversation", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("conversations").doc("conv_2").set({
      conversationId: "conv_2",
      members: [ALICE_UID, BOB_UID],
      lastMessage: "Assalamu Alaikum",
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("conversations").doc("conv_2").collection("messages").doc("msg_1").set({
      messageId: "msg_1",
      conversationId: "conv_2",
      senderId: ALICE_UID,
      text: "Assalamu Alaikum! Nice to connect with you.",
      createdAt: new Date(),
    })
  );
});

// --- Admin Access Test ---
test("Admin: can read reports", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("reports").doc("rep_1").set({
      reportId: "rep_1",
      reporterId: ALICE_UID,
      reportedUserId: BOB_UID,
      reason: "Inappropriate language",
      status: "open",
      createdAt: new Date(),
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: "tarunahmedsakal100@gmail.com" }).firestore();
  await assertSucceeds(
    adminDb.collection("reports").doc("rep_1").get()
  );
});

test("Admin by UID: can read reports directly", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("reports").doc("rep_uid").set({
      reportId: "rep_uid",
      reporterId: ALICE_UID,
      reportedUserId: BOB_UID,
      reason: "UID test",
      status: "open",
      createdAt: new Date(),
    });
  });

  const adminUidDb = testEnv.authenticatedContext("xWDJ4WM2qiZXYLCAO924hAJFDuz1").firestore();
  await assertSucceeds(
    adminUidDb.collection("reports").doc("rep_uid").get()
  );
});

