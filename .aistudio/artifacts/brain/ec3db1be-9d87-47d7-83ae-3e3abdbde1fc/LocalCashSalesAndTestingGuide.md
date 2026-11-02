# Local Testing, Sideloading & Cash-Sale Guide

This practical playbook guides you through testing the **GoalAlert** app on real physical phones in your country and selling it in person for cash before releasing it on the Google Play Store or Apple App Store.

---

## 1. How to Get the APK File from AI Studio

### Method A: Direct Export from the AI Studio Menu (Easiest)
1. In the top bar or settings menu of AI Studio, click **Export** / **Download APK**.
2. Download the `.apk` file directly to your computer or phone.

### Method B: From the Build Artifacts
The APK has been compiled and is ready at:
```
app/build/outputs/apk/debug/app-debug.apk
```
You can download this file directly through the file tree or export dialog.

---

## 2. How to Sideload & Install on Customers' Android Phones

Android allows direct installation of `.apk` files without the Google Play Store.

### Step 1: Send the File
Transfer `app-debug.apk` to the customer's phone using any of these methods:
- **WhatsApp or Telegram:** Send the APK directly as a document/file.
- **Nearby Share / Quick Share:** Instant phone-to-phone transfer.
- **USB OTG Flash Drive / Cable:** Plug directly into their phone.
- **Google Drive / MediaFire Link:** Let them tap download.

### Step 2: Enable "Install Unknown Apps"
When they tap the APK file, Android will prompt for security permission:
1. Tap **Settings** on the prompt.
2. Toggle ON **"Allow from this source"** (e.g., WhatsApp, Chrome, or My Files).
3. Tap **Install**.
4. If Google Play Protect asks to scan, tap **"Install anyway"**.

---

## 3. How to Protect Your Cash Sales (Activation Code System)

### The Risk of Raw APKs:
If you give someone an APK without protection, they can share it for free with all their friends via Bluetooth, ShareIt, or WhatsApp.

### The Solution (Already Built Into the App!):
We have built the **Local Cash Sales & Device Activation Toolkit** directly into the app (under the **Audio & Guide** tab):

1. **Each Device has a Unique Hardware ID** (e.g. `A7B2X9`).
2. When you install the app on a customer's phone:
   - Play the loud stadium roar to demonstrate the app.
   - Collect the cash payment.
   - Enter one of your Master Activation Passcodes to unlock it permanently:
     - `GOAL2026`
     - `EUROPRO`
     - `CASH777`
     - `VIPGOAL`
     - Or any custom code starting with `PASS-` (e.g., `PASS-001`, `PASS-002`)
3. Once unlocked, the app saves the activation locally in persistent storage. It will remain unlocked on their phone forever, but if they send the APK file to someone else, that new phone will start in locked mode until an activation code is provided!

---

## 4. How to Handle iPhone (iOS) Users Locally

Apple does **not** allow loose `.apk` installation on iPhones. Here is how to test and distribute to iPhone users before releasing to the public App Store:

### Option 1: TestFlight (Recommended & Official)
- **What it is:** Apple's official testing service.
- **Capacity:** Up to **10,000 external testers** using a simple shareable link!
- **How it works:**
  1. The user downloads the free **TestFlight** app from the App Store.
  2. You give them your TestFlight invite link or enter their email.
  3. They tap "Install" and test the app.
  4. You collect cash in person before sending the link.

### Option 2: Live Browser Preview (Instant Demo)
- You can open the **Development App URL** or **Shared App URL** on their iPhone Safari browser to showcase the live UI and goal alerts.

---

## 5. In-Person Cash Sales Script (Field Playbook)

1. **The Hook:**
   > *"Watch this: Whenever Haaland or Mbappé scores in the Premier League or Champions League, listen to what happens."*
2. **The Demonstration:**
   - Open **GoalAlert**.
   - Tap **"Test Alert"** -> **"⚡ TRIGGER RANDOM GOAL ALERT"**.
   - The loud stadium crowd cheers and the goal notification drops down with the score, scorer, and assist provider.
3. **The Close:**
   - Offer them lifetime access for your cash price (e.g., $5, $10, or local currency equivalent).
4. **The Hand-off:**
   - Sideload the APK to their device.
   - Enter your activation code (`GOAL2026`).
   - Confirm notifications and stadium cheer are working on their device!
