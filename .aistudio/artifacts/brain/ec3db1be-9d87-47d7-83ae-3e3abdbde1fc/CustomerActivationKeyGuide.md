# Customer Activation Key & PIN Generation Guide

This guide explains how to generate, manage, and verify unique activation keys/PINs for your customers when selling **GoalAlert Pro** locally for cash.

---

## 1. How the Activation System Works (Anti-Piracy)

To prevent customers from sharing an unlock code with unpaid friends, **GoalAlert Pro** binds each customer's 4-digit activation PIN to their unique **6-character Device ID**:

```
[ Customer Phone ]                [ Seller's Tool ]
1. Installs App                   4. Collects Cash
2. Sees Lock Screen               5. Enters Device ID: "A3F9D2"
3. Shares Device ID: "A3F9D2" ──> 6. Generates PIN: "4819"
                                  7. Gives PIN "4819" to Customer ──> 8. Unlocks Permanently!
```

- **Why it works**: The 4-digit PIN is calculated from `Device ID + Your Secret Admin Salt (munali0736)`.
- **Anti-Sharing Protection**: If Customer A shares `4819` with Customer B, Customer B has a different Device ID (e.g. `B7K42P`), so `4819` will fail on Customer B's phone. Only the PIN you generate for `B7K42P` will unlock it.

---

## 2. Three Ways to Generate Keys

### Method A: Directly Inside Your Own App (Built-In Generator)

You do **not** need any external tools—the key generator is built right into the app!

1. Open your own **GoalAlert Pro** phone (unlocked).
2. Scroll to the **Cash Sales & Passcode Lock** card at the bottom.
3. Look at the **🔑 Customer PIN Generator** box.
4. Type the customer's 6-character Device ID (e.g. `A3F9D2`).
5. Tap **Generate**.
6. The app instantly shows:
   - **CUSTOMER ACTIVATION PIN: `4819`**
   - Tap **"Copy PIN"** or read it aloud to the customer.
7. The customer types `4819` on their 4-dot numeric keypad. The app immediately unlocks!

---

### Method B: Offline Web Tool (Keep on Your Phone or Laptop)

Save the following single-file HTML as `key_generator.html` on your phone or laptop. You can open it in Chrome, Safari, or Firefox **with zero internet connection**:

```html
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>GoalAlert Pro - Customer PIN Generator</title>
<style>
  body {
    background: #070B14;
    color: #E6EDF3;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    display: flex;
    justify-content: center;
    padding: 24px;
    margin: 0;
  }
  .card {
    background: #101726;
    border: 2px solid #FFD700;
    border-radius: 16px;
    padding: 24px;
    max-width: 420px;
    width: 100%;
    box-shadow: 0 10px 30px rgba(0,0,0,0.7);
  }
  h2 { color: #FFD700; margin-top: 0; font-size: 22px; text-align: center; }
  p { color: #8B949E; font-size: 13px; line-height: 1.5; }
  label { font-size: 12px; color: #58A6FF; font-weight: bold; text-transform: uppercase; }
  input {
    width: 100%;
    box-sizing: border-box;
    background: #172238;
    border: 1px solid #30363D;
    border-radius: 8px;
    padding: 14px;
    font-size: 18px;
    color: #FFFFFF;
    letter-spacing: 2px;
    text-transform: uppercase;
    margin-top: 6px;
    margin-bottom: 16px;
  }
  input:focus { outline: none; border-color: #00E676; }
  button {
    width: 100%;
    background: #00E676;
    color: #003918;
    border: none;
    border-radius: 8px;
    padding: 14px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
  }
  button:hover { background: #00c864; }
  .result {
    margin-top: 20px;
    padding: 16px;
    background: #082914;
    border: 2px solid #00E676;
    border-radius: 10px;
    text-align: center;
    display: none;
  }
  .result-pin {
    font-size: 36px;
    font-weight: 900;
    letter-spacing: 8px;
    color: #FFFFFF;
    margin: 8px 0;
  }
  .tag { font-size: 11px; color: #8B949E; }
</style>
</head>
<body>

<div class="card">
  <h2>⚽ GoalAlert Pro Keymaker</h2>
  <p>Enter the customer's 6-character Device ID shown on their phone lock screen:</p>
  
  <label for="deviceId">Customer Device ID</label>
  <input type="text" id="deviceId" maxlength="6" placeholder="e.g. A3F9D2" />
  
  <button onclick="generatePin()">Generate Customer PIN</button>

  <div id="resultBox" class="result">
    <div style="color: #00E676; font-size: 12px; font-weight: bold;">ACTIVATION KEY FOR THIS PHONE:</div>
    <div id="pinDisplay" class="result-pin">----</div>
    <div class="tag">🔒 Device-Locked: Only works on this Device ID.</div>
  </div>
</div>

<script>
function generatePin() {
  const deviceId = document.getElementById("deviceId").value.trim().toUpperCase();
  if (!deviceId) {
    alert("Please enter the 6-character Device ID!");
    return;
  }
  const salt = "munali0736"; // Your secret master salt
  const seed = deviceId + ":" + salt;
  
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash * 37 + seed.charCodeAt(i)) & 0x7FFFFFFF;
  }
  const pin = 1000 + (hash % 9000);
  
  document.getElementById("pinDisplay").innerText = pin;
  document.getElementById("resultBox").style.display = "block";
}
</script>

</body>
</html>
```

---

### Method C: Master Admin PIN & Emergency Universal Codes

If you ever need to unlock a device instantly in person without generating a custom device PIN, you have full universal control:

| Key Type | Code | Where to Enter | Use Case |
| :--- | :--- | :--- | :--- |
| **Master Admin PIN** | `munali0736` | Tap **Admin PIN** button on Lock Screen | Unlocks any device permanently as the owner |
| **Numeric Shortcut** | `0736` | Direct on 4-dot Keypad | Fast testing or demo units |
| **Year Fallback** | `2026` | Direct on 4-dot Keypad | Universal seller backup code |
| **Batch Code 1** | `7788` | Direct on 4-dot Keypad | Seller staff / family code |
| **Batch Code 2** | `9900` | Direct on 4-dot Keypad | Emergency field override |

---

## 3. Python Script for Generating Pre-Printed Scratch Cards

If you want to sell **scratch cards** or paper receipts in local shops, run this Python script to batch-generate PINs for a list of customer device IDs:

```python
def generate_pin(device_id: str, salt: str = "munali0736") -> str:
    clean_id = device_id.strip().upper()
    seed = f"{clean_id}:{salt}"
    hash_val = 0
    for char in seed:
        hash_val = (hash_val * 37 + ord(char)) & 0x7FFFFFFF
    return str(1000 + (hash_val % 9000))

# Example usage:
customer_devices = ["A3F9D2", "B4810C", "C7E1B4", "D902FA", "E11409"]
for dev in customer_devices:
    print(f"Device: {dev} -> Activation PIN: {generate_pin(dev)}")
```

---

## 4. In-the-Field Customer Walkthrough (Recommended Script)

1. **Demonstrate First**:
   - Tap **"🔊 Sound Demo"** on the lock screen so the customer hears the stadium roar and whistles.
   - Show them the live match cards and team logos through the preview.

2. **Collect Payment**:
   - Collect cash (e.g. $5 / $10 / 50 Kwacha / 2000 Naira / local currency).

3. **Get Device ID**:
   - Ask the customer to read their 6-character **Device ID** displayed in blue on the lock screen (they can also tap it to copy).

4. **Generate & Unlock**:
   - Type their Device ID into your in-app generator (or the offline HTML tool).
   - Enter or tell them their unique 4-digit PIN.
   - When entered on the 4 dots, the screen flashes green and opens **GoalAlert Pro** permanently!
