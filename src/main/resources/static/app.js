const $=s=>document.querySelector(s);
let page='dashboard', currentUser=null;
async function api(url,opts={}){const r=await fetch('/api'+url,{headers:{'Content-Type':'application/json'},credentials:'same-origin',...opts});let d;try{d=await r.json()}catch{d={}}if(!r.ok)throw new Error(d.error||'Request failed');return d}
function toast(msg){const t=$('#toast');t.textContent=msg;t.classList.add('show');setTimeout(()=>t.classList.remove('show'),2500)}
$('#loginForm').addEventListener('submit',async e=>{e.preventDefault();try{currentUser=await api('/login',{method:'POST',body:JSON.stringify({username:$('#username').value,password:$('#password').value})});showApp()}catch(err){$('#loginError').textContent=err.message}});
async function showApp(){
  $('#loginView').classList.add('hidden');
  $('#app').classList.remove('hidden');
  $('#who').textContent=currentUser.fullName;
  $('#userBadge').textContent=currentUser.role;
  const rolePages={
    ADMIN:['dashboard','patients','tests','medicines','billing','departments','staff'],
    REGISTRATION:['dashboard','patients','billing'],
    DOCTOR:['dashboard','patients','tests'],
    TECHNICIAN:['dashboard','tests'],
    PHARMACY:['dashboard','medicines'],
    PATIENT:['dashboard','portal']
  };
  const allowed=rolePages[currentUser.role]||['dashboard'];
  document.querySelectorAll('.nav').forEach(b=>{
    b.classList.toggle('hidden',!allowed.includes(b.dataset.page));
    b.onclick=()=>render(b.dataset.page);
  });
  await render('dashboard');
}
$('#logout').onclick=async()=>{await api('/logout',{method:'POST'});location.reload()};
async function render(p){
  page=p;
  document.querySelectorAll('.nav').forEach(b=>b.classList.toggle('active',b.dataset.page===p));
  $('#pageTitle').textContent=({dashboard:'Dashboard',patients:'Patient Management',tests:'Laboratory & Investigation',medicines:'Drug Stock Management',billing:'Billing & Invoices',departments:'Departments',staff:'Staff Accounts',portal:'My Patient Portal'})[p]||'Dashboard';
  try{
    if(p==='dashboard') await dashboard();
    else if(p==='patients') await patients();
    else if(p==='tests') await tests();
    else if(p==='medicines') await medicines();
    else if(p==='billing') await billing();
    else if(p==='departments') await departments();
    else if(p==='staff') await staff();
    else if(p==='portal') await patientPortal();
  }catch(e){$('#content').innerHTML=`<div class="panel error">${e.message}</div>`}
}
function stat(label,num,hint){return `<div class="stat"><div class="label">${label}</div><div class="num">${num}</div><div class="hint">${hint}</div></div>`}
async function dashboard(){
  if(currentUser.role==='PATIENT'){await patientPortal();return}
  if(currentUser.role==='REGISTRATION'){
    const d=await api('/dashboard');
    $('#content').innerHTML=`<div class="stats">${stat('Registered patients',d.patients,'Patient records')}${stat('Bills generated',d.bills,'Invoices recorded')}${stat('Investigation orders',d.tests,'All departments')}${stat('System status','Active','Registration desk connected')}</div>
    <div class="panel"><h3>Receptionist workspace</h3><p class="muted">Register patients, review patient details and prepare OPD, IPD or Emergency invoices.</p><div class="toolbar"><button class="btn blue" onclick="render('patients')">Register / View Patients</button><button class="btn" onclick="render('billing')">Open Billing</button></div></div>`;
    return;
  }
  if(currentUser.role==='DOCTOR'){
    const [p,t]=await Promise.all([api('/patients'),api('/tests')]);
    const myName=(currentUser.fullName||'').toLowerCase();
    const assigned=p.filter(x=>!x.doctor||x.doctor.toLowerCase().includes(myName)||myName.includes(x.doctor.toLowerCase()));
    $('#content').innerHTML=`<div class="stats">${stat('Patient records',p.length,'Available clinical records')}${stat('Investigation orders',t.length,'Prescribed investigations')}${stat('Pending results',t.filter(x=>x.status!=='COMPLETED').length,'Awaiting laboratory')}${stat('Completed results',t.filter(x=>x.status==='COMPLETED').length,'Reports available')}</div>
    <div class="panel"><h3>Doctor workspace</h3><p class="muted">Review patient history, prescribe investigations and follow up on reports.</p><button class="btn blue" onclick="render('patients')">View Patients</button> <button class="btn" onclick="render('tests')">Manage Investigations</button></div>`;
    return;
  }
  if(currentUser.role==='TECHNICIAN'){
    const t=await api('/tests');
    $('#content').innerHTML=`<div class="stats">${stat('Total orders',t.length,'All investigation orders')}${stat('Pending',t.filter(x=>x.status!=='COMPLETED').length,'Awaiting processing')}${stat('Completed',t.filter(x=>x.status==='COMPLETED').length,'Results submitted')}${stat('Department','Laboratory','Technician workspace')}</div>
    <div class="panel"><h3>Laboratory workspace</h3><p class="muted">Open investigation orders, enter findings and submit completed reports to the doctor.</p><button class="btn blue" onclick="render('tests')">Open Investigation Queue</button></div>`;
    return;
  }
  if(currentUser.role==='PHARMACY'){
    const m=await api('/medicines');
    const low=m.filter(x=>x.quantity<=10).length;
    $('#content').innerHTML=`<div class="stats">${stat('Medicine items',m.length,'Stock keeping units')}${stat('Low stock alerts',low,'Items at or below 10')}${stat('Total units',m.reduce((n,x)=>n+x.quantity,0),'Units in dispensary')}${stat('Department','Dispensary','Pharmacy workspace')}</div>
    <div class="panel"><h3>Pharmacy workspace</h3><p class="muted">Monitor medicine availability, add stock and record stock adjustments.</p><button class="btn blue" onclick="render('medicines')">Manage Medicine Stock</button></div>`;
    return;
  }
  const d=await api('/dashboard');
  $('#content').innerHTML=`<div class="stats">${stat('Registered patients',d.patients,'Patient records')}${stat('Investigation orders',d.tests,'All test categories')}${stat('Medicine items',d.medicines,'Stock keeping units')}${stat('Bills generated',d.bills,'Invoices recorded')}</div><div class="section-head"><h3>Operational overview</h3></div><div class="stats">${stat('Pending investigations',d.pendingTests,'Awaiting completion')}${stat('Low stock alerts',d.lowStock,'Items at or below 10')}${stat('System status','Active','Application connected')}${stat('Signed in as',currentUser.role,'Current access role')}</div><div class="panel"><h3>Welcome to MLDIS</h3><p class="muted">Manage patient registration, clinical investigations, medicine inventory, departments, staff and billing from the navigation menu.</p></div>`;
}
async function patientPortal(){
  const d=await api('/patient/portal');
  const p=d.patient;
  $('#content').innerHTML=`<div class="stats">${stat('Patient ID',p.patientCode,'Your unique hospital ID')}${stat('Investigation orders',d.tests.length,'Your test records')}${stat('Bills',d.bills.length,'Your invoices')}${stat('Reports ready',d.tests.filter(x=>x.status==='COMPLETED').length,'Completed investigations')}</div>
  <div class="panel"><h3>My profile</h3><div class="stats" style="grid-template-columns:repeat(2,minmax(0,1fr))">
    <div><div class="label">Full name</div><b>${p.fullName}</b></div><div><div class="label">Phone</div><b>${p.phone||'-'}</b></div>
    <div><div class="label">Department</div><b>${p.department||'-'}</b></div><div><div class="label">Consulting doctor</div><b>${p.doctor||'-'}</b></div>
    <div><div class="label">Date of birth</div><b>${p.dateOfBirth||'-'}</b></div><div><div class="label">Email</div><b>${p.email||'-'}</b></div>
  </div></div>
  <div class="section-head"><h3>My investigation results</h3></div>
  ${table(['Category','Test','Prescribed by','Status','Result'],d.tests.map(x=>`<tr><td>${x.testType}</td><td>${x.testName}</td><td>${x.prescribedBy||'-'}</td><td><span class="badge ${x.status==='COMPLETED'?'green':'orange'}">${x.status}</span></td><td>${x.result||'Not available yet'}</td></tr>`))}
  <div class="section-head"><h3>My bills</h3></div>
  ${table(['Invoice','Visit type','Total','Status'],d.bills.map(x=>`<tr><td>${x.billNumber}</td><td>${x.visitType}</td><td>₹${Number(x.total).toFixed(2)}</td><td>${x.status}</td></tr>`))}
  <p class="muted">For corrections to your personal details, contact the hospital registration desk.</p>`;
}
function openModal(title, fields, onSave) {
  const modal = document.createElement('div');
  modal.className = 'modal';

  const fieldMarkup = fields.map(f => {
    let control = '';
    if (f.type === 'select') {
      control = `<select name="${f.name}">${f.options.map(o => `<option>${o}</option>`).join('')}</select>`;
    } else if (f.type === 'textarea') {
      control = `<textarea name="${f.name}" ${f.required ? 'required' : ''}></textarea>`;
    } else {
      const required = f.required ? 'required' : '';
      const value = f.value !== undefined ? `value="${f.value}"` : '';
      const step = f.step ? `step="${f.step}"` : '';
      control = `<input name="${f.name}" type="${f.type || 'text'}" ${required} ${value} ${step}>`;
    }
    return `<div class="${f.wide ? 'wide' : ''}"><label>${f.label}</label>${control}</div>`;
  }).join('');

  modal.innerHTML = `
    <div class="modal-card">
      <div class="modal-head">
        <h3>${title}</h3>
        <button class="btn" id="closeModal" type="button">✕</button>
      </div>
      <form id="dataForm">
        <div class="form-grid">${fieldMarkup}</div>
        <div class="modal-actions">
          <button type="button" class="btn" id="cancelModal">Cancel</button>
          <button type="submit" class="btn blue">Save record</button>
        </div>
      </form>
    </div>`;
  document.body.appendChild(modal);

  const close = () => modal.remove();
  modal.querySelector('#closeModal').onclick = close;
  modal.querySelector('#cancelModal').onclick = close;
  modal.querySelector('#dataForm').onsubmit = async e => {
    e.preventDefault();
    const obj = Object.fromEntries(new FormData(e.target).entries());
    for (const k in obj) {
      if (k === 'quantity') obj[k] = parseInt(obj[k] || 0, 10);
      if (['fee', 'unitPrice', 'taxPercent', 'discount', 'consultationCharge', 'testCharge', 'medicineCharge'].includes(k)) {
        obj[k] = parseFloat(obj[k] || 0);
      }
    }
    try {
      await onSave(obj);
      close();
      toast('Record saved successfully');
      await render(page);
    } catch (err) {
      toast(err.message);
    }
  };
}

function table(headers,rows){return `<div class="panel table-wrap"><table><thead><tr>${headers.map(h=>`<th>${h}</th>`).join('')}</tr></thead><tbody>${rows.length?rows.join(''):`<tr><td colspan="${headers.length}" class="empty">No records found</td></tr>`}</tbody></table></div>`}
async function patients(){const a=await api('/patients');$('#content').innerHTML=`<div class="section-head"><h3>Patient register</h3><button class="btn blue" id="add">＋ Register patient</button></div>${table(['Patient ID','Name','Phone','Gender','Department','Doctor'],a.map(x=>`<tr><td><b>${x.patientCode}</b></td><td>${x.fullName}</td><td>${x.phone||'-'}</td><td>${x.gender||'-'}</td><td>${x.department||'-'}</td><td>${x.doctor||'-'}</td></tr>`))}`;$('#add').onclick=()=>openModal('Register new patient',[{name:'patientCode',label:'Patient ID (leave blank for auto ID)'},{name:'fullName',label:'Full name',required:true},{name:'phone',label:'Phone'},{name:'email',label:'Email',type:'email'},{name:'gender',label:'Gender',type:'select',options:['Not specified','Female','Male','Other']},{name:'dateOfBirth',label:'Date of birth',type:'date'},{name:'address',label:'Address',wide:true},{name:'department',label:'Department'},{name:'doctor',label:'Consulting doctor'},{name:'medicalHistory',label:'Medical history / notes',type:'textarea',wide:true}],o=>api('/patients',{method:'POST',body:JSON.stringify(o)}))}
async function tests(){const a=await api('/tests');$('#content').innerHTML=`<div class="section-head"><h3>Investigation orders and results</h3><button class="btn blue" id="add">＋ Prescribe investigation</button></div>${table(['Patient','Category','Test','Prescribed by','Status','Result / Action'],a.map(x=>`<tr><td>${x.patientName} (${x.patientCode})</td><td>${x.testType}</td><td>${x.testName}</td><td>${x.prescribedBy||'-'}</td><td><span class="badge ${x.status==='COMPLETED'?'green':'orange'}">${x.status}</span></td><td><button class="btn" onclick="completeTest(${x.id})">Update result</button></td></tr>`))}`;$('#add').onclick=()=>openModal('Create investigation order',[{name:'patientCode',label:'Patient ID',required:true},{name:'patientName',label:'Patient name',required:true},{name:'testType',label:'Test category',type:'select',options:['Laboratory','Radiology','Pathology']},{name:'testName',label:'Test name',required:true},{name:'prescribedBy',label:'Prescribed by'},{name:'fee',label:'Fee (INR)',type:'number',step:'0.01'}],o=>api('/tests',{method:'POST',body:JSON.stringify(o)}))}
window.completeTest=async id=>{const result=prompt('Enter test result / report summary:');if(result===null)return;try{await api('/tests/'+id,{method:'PUT',body:JSON.stringify({status:'COMPLETED',result})});toast('Investigation report updated');await render('tests')}catch(e){toast(e.message)}}
async function medicines(){const a=await api('/medicines');$('#content').innerHTML=`<div class="section-head"><h3>Medicine inventory</h3><button class="btn blue" id="add">＋ Add medicine</button></div>${table(['Code','Medicine','Batch','Available qty','Unit price','Expiry','Stock action'],a.map(x=>`<tr><td>${x.medicineCode}</td><td><b>${x.name}</b></td><td>${x.batchNo||'-'}</td><td><span class="badge ${x.quantity<=10?'orange':'green'}">${x.quantity}</span></td><td>₹${Number(x.unitPrice).toFixed(2)}</td><td>${x.expiryDate||'-'}</td><td><button class="btn" onclick="changeStock(${x.id})">Adjust stock</button></td></tr>`))}`;$('#add').onclick=()=>openModal('Add medicine to stock',[{name:'medicineCode',label:'Medicine code'},{name:'name',label:'Medicine name',required:true},{name:'batchNo',label:'Batch number'},{name:'quantity',label:'Opening quantity',type:'number'},{name:'unitPrice',label:'Unit price (INR)',type:'number',step:'0.01'},{name:'expiryDate',label:'Expiry date',type:'date'}],o=>api('/medicines',{method:'POST',body:JSON.stringify(o)}))}
window.changeStock=async id=>{const q=prompt('Enter stock adjustment (use positive number to add, negative to issue):');if(q===null)return;try{await api('/medicines/'+id+'/stock',{method:'PUT',body:JSON.stringify({quantity:Number(q)})});toast('Stock updated');await render('medicines')}catch(e){toast(e.message)}}
async function billing(){const a=await api('/bills');$('#content').innerHTML=`<div class="section-head"><h3>Patient invoices</h3><button class="btn blue" id="add">＋ Generate bill</button></div>${table(['Invoice','Patient','Visit type','Subtotal components','Discount','Tax','Total','Status'],a.map(x=>`<tr><td>${x.billNumber}</td><td>${x.patientName} (${x.patientCode})</td><td>${x.visitType}</td><td>₹${(x.consultationCharge+x.testCharge+x.medicineCharge).toFixed(2)}</td><td>₹${x.discount.toFixed(2)}</td><td>${x.taxPercent}%</td><td><b>₹${x.total.toFixed(2)}</b></td><td><span class="badge green">${x.status}</span></td></tr>`))}`;$('#add').onclick=()=>openModal('Generate patient bill',[{name:'patientCode',label:'Patient ID',required:true},{name:'patientName',label:'Patient name',required:true},{name:'visitType',label:'Visit type',type:'select',options:['OPD','IPD','Emergency']},{name:'consultationCharge',label:'Consultation charge (INR)',type:'number',step:'0.01'},{name:'testCharge',label:'Investigation charges (INR)',type:'number',step:'0.01'},{name:'medicineCharge',label:'Medicine charges (INR)',type:'number',step:'0.01'},{name:'discount',label:'Discount (INR)',type:'number',step:'0.01'},{name:'taxPercent',label:'Tax (%)',type:'number',step:'0.01',value:0}],o=>api('/bills',{method:'POST',body:JSON.stringify(o)}))}
async function departments(){const a=await api('/departments');$('#content').innerHTML=`<div class="section-head"><h3>Hospital departments</h3><button class="btn blue" id="add">＋ Add department</button></div>${table(['Department','Description'],a.map(x=>`<tr><td><b>${x.name}</b></td><td>${x.description||'-'}</td></tr>`))}`;$('#add').onclick=()=>openModal('Add department',[{name:'name',label:'Department name',required:true},{name:'description',label:'Description',type:'textarea'}],o=>api('/departments',{method:'POST',body:JSON.stringify(o)}))}
async function staff(){const a=await api('/staff');$('#content').innerHTML=`<div class="section-head"><h3>Staff accounts and roles</h3><button class="btn blue" id="add">＋ Create account</button></div>${table(['Name','Username','Role','Department','Linked Patient ID'],a.map(x=>`<tr><td><b>${x.fullName}</b></td><td>${x.username}</td><td><span class="badge">${x.role}</span></td><td>${x.department||'-'}</td><td>${x.linkedPatientCode||'-'}</td></tr>`))}`;$('#add').onclick=()=>openModal('Create staff account',[{name:'fullName',label:'Full name',required:true},{name:'username',label:'Username',required:true},{name:'password',label:'Temporary password',required:true},{name:'role',label:'Role',type:'select',options:['ADMIN','REGISTRATION','DOCTOR','TECHNICIAN','PHARMACY','PATIENT']},{name:'department',label:'Department / Unit'},{name:'linkedPatientCode',label:'Linked Patient ID (required for PATIENT role)'}],o=>api('/staff',{method:'POST',body:JSON.stringify(o)}))}
(async()=>{try{const me=await api('/me');if(me.authenticated){currentUser={fullName:me.fullName||me.username,username:me.username,role:me.role,linkedPatientCode:me.linkedPatientCode};showApp()}}catch{}})();
