A3Maker = A3Maker || {};
$(function () {
	A3Maker.propertyWindow = {
		isShown: false,
		propertyWindow: $('#element'),
		propertyType: "",
		propertyTypes: ['page', 'image', 'text', 'link', 'order', 'popup', 'list', 'listrow'],
		name: '',
		isPositionFixed: true,
		positionX: 20,
		positionY: 100,
		propertyWindowWidth: 0,

		//jquey selector object
		$name: $('#element .field_value_name'),
		$x: $('#element .field_value_x'),
		$y: $('#element .field_value_y'),
		$width: $('#element .field_value_w'),
		$height: $('#element .field_value_h'),
		$fixRatio: $('#element .field_value_owhyn'),
		$fixed: $('#element .field_value_fixed'),
		$fontSelect: $('#element .field_value_fontface'),
		
		$fontSizeSelect: $('#element .field_value_size'),
		$fontWeightSelect: $('#element .field_value_font_weight'),
		$colorPicker: $('#element .field_value_color'),
		$textContent: $('#element .field_value_content'),
		$backColorPicker: $('#element .field_value_backcolor'),
		$lineColorPicker : $('#element .field_value_line_color'),
		$fillStylePicker : $('#element .field_value_fill_style'),

		//link 
		$pageTypeSelect: $('#element .field_value_linktype'),

		$linkPageSelectDiv: $('#element .linkPageSelectDiv'),
		$linkPopupSelectDiv: $('#element .linkPopupSelectDiv'),

		$linkPageSelect: $('#element .field_value_linkpage'),
		$linkPopupSelect: $('#element .field_value_linkpopup'),
		$linkWebText: $('#element .field_value_linkweb'),
		$phoneNumberText: $(".field_value_linkphonenumber"),

		$youTubeText: $(".field_value_linkyoutube"),
		$mallInMallText: $(".field_value_linkmallinmall"),
		$emailText: $(".field_value_linkEmail"),
		$mapDiv: $(".link_map"),
		$mapText: $('.field_value_linkmap'),

		//order
		$menuName: $('#element .field_value_menu_name'),
		$menuCode: $('#element .field_value_menu_code'),
		$menuPrice: $('#element .field_value_menu_price'),
		$menuDes: $('#element .field_value_menu_des'),
		$menuDiscount: $('#element .field_value_menu_discount'),
		$menuStock: $('#element .field_value_menu_stock'),
		$storeCode: $('#element .field_value_store_code'),
		$menuGroupName: $('#element .field_value_menu_group_name'),
		
		orderChangeBtn: $('#element .menuLoading_btn'),
		
		/* upload*/
		$uploadFake: $('div[name="uploadFake"]'),
		$uploadImgSelector: $('#attach'),
		$uploadImageFrm: $('.upload_image_frm'),
		
		$radiusHandle : $("#radius_handle"),
		$opacityHandle : $("#opacity_handle"),
		$linewidthHandle : $("#linewidth_handle"),

		$lineCapSelect: $('#element .field_value_linecap'),
		$lineJoinSelect: $('#element .field_value_linejoin'),
		initPane : false,
		/* tweanpan element*/
		pane : null,
		
		generalFolder : null,
		PARAMS  :   {
			top : 0,
			left : 0,
			width : 0,
			height : 0,
			color : "#000000",
			name : "ahn",
			"background-color" : '#FFFFFF',
			opacity : 1,
			"border-radius" : 0,
			'font-width' : 10,
			scale : "25",
			fixRatio : true,
			mouseLock : false,
			"font-color" : "#0000000",
			"font-size" : "8",
			"font-family" : '굴림, Gulim, Arial, sans-serif',
			
			text : "",
			linkType : "",
			position : "",
			linkTarget : "",

			menuGroupCode : "M1",
			menuGroupName: "한식",
			storeCode : "S1",
			menuName: "라면",
			menuPrice: 110000,
			menuCode : "S1_M1_C1",
			menuDes: "시그니쳐 음식",
			menuUnit: "원",
			menuStock : 100,
			menuDiscount: 1000000,
		
			strokeStyle : "#000000",
			fillStyle : "#FFFFFF",
			lineWidth : 8,
			lineJoin : 'round',
			lineCap : 'round',
			isDrawing : false,
			isErasing : false,
			attention: `판서는 전체 화면을 가리고 있어요.다른 컴포넌트를 선택하려면 좌측 트리에서 컴포넌트를 선택하세요`,
			autoEvent : false,
			
			resolutionType : "VGA",
			micVolume : 5,
			useAudio : true, // 오디오 녹음 여부
			useVidwo : true,   // 영상은 항상 녹화하기 때문에 true 고정 
			videoStatus : null ,
			facingMode : "user"   
		},

		initControl: function(paramData){
		
		},

		initControl1 : function () {
			var context = this;
			this.$uploadFake.css('cursor', 'pointer');
			this.propertyWindowWidth = this.propertyWindow.width();
			this.propertyWindow.draggable({
				containment: "#vertical_table",
				start: function () {
					context.isPositionFixed = true;
				},
				drag: function () {
					context.isPositionFixed = true;
				},
				stop: function () {
					if (context.isPositionFixed) {
						var offset = $(this).position();
						context.positionX = offset.left;
						context.positionY = offset.top;
					}
				}
			});
			this.initializeEventHandler();
		},

		showPropertyWindow: function (propertyType) {

			
			this,propertyType = propertyType;
			var context = this;
			var data = null;
			if (propertyType == "page"){
				data = A3Maker.getSelectedPage().data;
			}else {
				data = A3Maker.getSelectedComponent().data;
			}
			
			// 기본 옵션과 합침
			Object.assign(this.PARAMS, data);

			//숫자가 와야 할 곳에 문자가 오는 경우도 있으므로 해당 값의 타입을 변경
			this.PARAMS['font-weight'] = parseInt(this.PARAMS['font-weight']);
			this.PARAMS['menuPrice'] = parseInt(this.PARAMS['menuPrice']);
			this.PARAMS['menuStock'] = parseInt(this.PARAMS['menuStock']);

			this.pane = A3Maker.pane;
			this.pane.hidden = false;
			this.pane.title = propertyType.toUpperCase()+ " COMPONENT";
			this.pane.element.querySelector('.tp-rotv_t').style.color = "#cccccc";
			this.pane.element.querySelector('.tp-rotv_t').style.fontSize = "13px";
			this.pane.element.querySelector('.tp-rotv_t').style.fontWeight = "bold";

			if (this.generalFolder){
				this.generalFolder.dispose();
				this.generalFolder = null;
			}
			
			if (this.linkFolder){
				this.linkFolder.dispose();
				this.linkFolder = null;
			}

			if (this.textFolder){
				this.textFolder.dispose();
				this.textFolder = null;
			}
			if (this.orderFolder){
				this.orderFolder.dispose();
				this.orderFolder = null;
			}

			if (this.canvasFolder){
				this.canvasFolder.dispose();
				this.canvasFolder = null;
			}
		

			if (this.recorderFolder){
				this.recorderFolder.dispose();
				this.recorderFolder = null;
			}
		
			this.generalFolder = this.pane.addFolder({title : "GENERAL SETTING"})
			this.nameBinding = this.generalFolder.addBinding(this.PARAMS, "name",{label : "NAME"}).on('change', (ev)=> {
				console.log(this.PARAMS.name);
				context.changeComponentName({name : context.PARAMS.name});
			  });

			  this.nameBinding.element.querySelector('input').style.border = "1px solid #999999";
			  this.nameBinding.element.querySelector('input').style.color = "#cccccc";
			
			this.xBinding =  this.generalFolder.addBinding(this.PARAMS, "left",{label : "X POS", format : (number=> number.toFixed(0))})
				.on('change', (ev)=> context.updatePositionAndWidth());
				
			//console.log("this.xBinding ");
			//console.log(this.xBinding) ;
			this.xBinding.element.querySelector('input').style.color = "#cccccc";
			this.xBinding.element.querySelector('input').style.textAlign = "start";  
			
			this.yBinding = this.generalFolder.addBinding(this.PARAMS, "top",{label : "Y POS", format : (number=> number.toFixed(0))})
				.on('change', (ev)=> context.updatePositionAndWidth());
			this.yBinding.element.querySelector('input').style.color = "#cccccc";  
			this.yBinding.element.querySelector('input').style.textAlign = "start";  
			
			this.widthBinding = this.generalFolder.addBinding(this.PARAMS, "width",{label : "WIDTH", format : (number=> number.toFixed(0))})
				.on('change', (ev)=> context.updatePositionAndWidth());
			this.widthBinding.element.querySelector('input').style.color = "#cccccc";  
			this.widthBinding.element.querySelector('input').style.textAlign = "start";  
			
			this.heigthBinding = this.generalFolder.addBinding(this.PARAMS, "height",{label : "HEIGHT", format : (number=> number.toFixed(0))})
				.on('change', (ev)=> context.updatePositionAndWidth());
			this.heigthBinding.element.querySelector('input').style.color = "#cccccc";  
			this.heigthBinding.element.querySelector('input').style.textAlign = "start";  
			
			this.mouseLockBinding = this.generalFolder.addBinding(this.PARAMS, 'mouseLock',{label : "MOUSE LOCK"}).on('change', (ev)=> {})
				.on("change", (ev)=> context.updateData({"mouseLock" : ev.value}))
			
			this.fixRatioBinding=  this.generalFolder.addBinding(this.PARAMS, 'fixRatio',{label : "FIX RATIO"})
				.on('change', (ev)=> {}).on("change", (ev)=> context.updateData({"fixRatio" : ev.value}));
			  
			//this.generalFolder.addInput(PARAMS, "Opacity", { min: 300, max: 1200, step: 10, label: "Opacity" })
			this.opacityBinding = this.generalFolder.addBinding(this.PARAMS, 'opacity', {
				step: 0.1,
				min: 0,
				max: 1,
				label : "OPACITY"
				}).on('change', (ev)=> context.updateData({"opacity" : ev.value}));

			this.borderRadiusBinding= this.generalFolder.addBinding(this.PARAMS, 'border-radius', {
				step: 1,
				min: 0,
				max: 50,
				label : "RADIUS"})
					.on('change', (ev)=> context.updateData({"border-radius" : ev.value}))

				
			if (propertyType != 'link' && propertyType != "image" && propertyType != "order" && propertyType != "camera"){
				this.backColorBinding = this.generalFolder.addBinding(
					this.PARAMS, "background-color", {view : "color", color: {alpha: true},label : 'BACK COLOR'})
					.on('change', function(ev){
						var color = ev.value;
						if (A3Maker.getSelectedPage()) {
							if (A3Maker.getSelectedComponent()) {
								A3Maker.getSelectedComponent().setData({ "background-color" : color });
							} else {
								var selectedPage = A3Maker.getSelectedPage();
								selectedPage.setData({ "background-color" : color });
								selectedPage.refresh();
							}
						}

					});
			}
			this.$uploadImgSelector.change(function (event) {

				if (A3Maker.mode == "test") {
					var file = event.target.files[0];

					$('#abmsProjectNo').val(A3Maker.project.projectSeq); // project_seq insert
					const reader = new FileReader();
					reader.readAsDataURL(file);
					reader.onload = function (e) {

						if (A3Maker.getSelectedPage()) {
							if (A3Maker.getSelectedComponent()) {
								A3Maker.getSelectedComponent().setData({ 'background-image': e.target.result });
							} else {
								A3Maker.getSelectedPage().data['background-image'] = e.target.result;
								A3Maker.getSelectedPage().$pageHolderSelector.css('background-image', 'url(' + e.target.result + ')')
							}
						}

						context.$uploadImgSelector.val("");
						context.isUploading = false;
						A3Maker.sidebar.updateSideThumbnailView();
					}
				} else {
					$('#abmsProjectNo').val(A3Maker.project.projectSeq); // project_seq insert
					context.$uploadImageFrm.submit();
				}

			});

			this.generalButtonBinding = this.generalFolder.addBlade({
				view: 'buttongrid',
				size: [2, 2],
				cells: (x, y) => ({
					title: [
					['BACKGROUND IMAGE', 'DOWNLOAD IMAGE'],
					['RESET IMAGE',  'CLIP ART'],
					][y][x],
				})})
				.on('click' ,(ev) => {
			//버튼의 문자열을 가져온 후 버튼의 타이틀을 변경함.	 
					//console.log(ev.cell.title);
					switch(ev.cell.title){
						case 'BACKGROUND IMAGE':
							context.$uploadImgSelector.trigger('click');
						break;
							break;
						case 'DOWNLOAD IMAGE':
							var target = A3Maker.getSelectedComponent() ? A3Maker.getSelectedComponent() : A3Maker.getSelectedPage();
						if (target.data['background-image'] != '' && target.data['background-image'].startsWith('/')) {
							if (confirm("이미지를 다운로드 하시겠습니까")) {
								console.log("다운로드 파일 : " + target.src);
								if ($("#ifrm").length > 0) $("#ifrm").remove();
								var url = '/abms/A3Maker/downloadResource?projectSeq=' + A3Maker.project.projectSeq + "&fileName=" + target.src;
								var html = "<iframe style ='width:0px;height:0px' id='ifrm' src='" + url + "'>";
								$("body").append(html);
							}
						} else {
							if (target.data['background-image'].startsWith('data:image')) {
								var fileExt = target.data['background-image'].split(";")[0].split("/")[1];

								if (fileExt == "jpeg") {
									fileExt = "jpg";
								}

								var fileName = fileName =
									A3Maker.project.projectName.replace(/ /g, '_') + '_' + $.datepicker.formatDate('yymmdd', new Date()) + "." + fileExt;
								console.log(fileName);
								// if (target.data['background-image'].indexOf('data:image/jpg') >=0 || target.data['background-image'].indexOf('data:image/jpeg') >=0 ){
								// 	fileName = A3Maker.project.projectName.replace(/ /g, '_') +'_'+ $.datepicker.formatDate('yymmdd', new Date()) + '.jpg';
								// 	//console.log(fileName)
								// }

								// if (target.data['background-image'].indexOf('data:image/png') >=0 ){
								// 	fileName = A3Maker.project.projectName.replace(/ /g, '_') +'_'+ $.datepicker.formatDate('yymmdd', new Date()) + '.png';
								// 	//console.log(fileName)
								// }
								var downLoadTag = document.createElement("a");
								downLoadTag.setAttribute('href', target.data['background-image']);
								downLoadTag.setAttribute('download', fileName);
								downLoadTag.setAttribute('id', "tempDownLoadTag");
								document.body.appendChild(downLoadTag);
								downLoadTag.click();
								document.body.removeChild(downLoadTag);
							} else {
								alert("다운로드할 이미지가 없습니다");
							}
						}
						break;
							break;
						case 'RESET IMAGE':
							var target = A3Maker.getSelectedComponent() ? A3Maker.getSelectedComponent() : A3Maker.getSelectedPage();
						
							if (!target.data['background-image'] || target.data['background-image'].trim().length < 1 || target.data['background-image'].trim() == target.defaultsrc || target.data['background-image'] == "") {
								alert("설정되어 있는 이미지가 없습니다");
								return;
							}
							if (confirm("설정된 이미지가 초기화 됩니다.") == true) {
								if (A3Maker.getSelectedPage()) {
									if (A3Maker.getSelectedComponent()) {
										A3Maker.getSelectedComponent().setData({'background-image': '', 'border-radius' : 0});
									} else {
										A3Maker.getSelectedPage().setData({'background-image': ''});
									}
								}
								A3Maker.sidebar.updateSideThumbnailView();
							}
							break;
						case 'CLIP ART':
							A3Maker.clipArt.show();
						break;
							break;
					}
				}
			);
			
			if (propertyType == "link"){
			
				this.linkFolder = this.pane.addFolder({title : "LINK SETTING"});
				this.linkTypeSeletBinding =  this.linkFolder.addBlade({view: 'list', label: 'LINK TYPE',
					options: [
						{text: '페이지 연결', value: 'PageLink'},
						{text: '팝업', value: 'Popup'},
						// {text: '미디어', value: 'Media'},
						{text: '웹', value: 'Web'},
						{text: '유튜브', value: 'YouTube'},
						{text: '지도', value: 'Map'},
						{text: '이메일', value: 'Email'},
						{text: '전화번호', value: 'Phonenumber'}
					],
					value: this.PARAMS['linkType'] ? this.PARAMS['linkType'] : "PageLink"
				}).on('change', (ev)=> {
					//console.log('changed start');
					
					/*ink.emitter_.emit('change', {value : PARAMS['linkType'] ? PARAMS['linkType'] : "PageLink", init : true}) 
					 를 통해 강제로 이벤트 발생하여 타켓 셀렉트 박스를 변경함 
					 이경우 지정한  value 값과 init 값을 참조하여 적절한 작업을 수행
					 */
					//console.log(ev.value);
					//console.log('이벤트 객체');
					//console.log(ev);
					context.linkTargetSelectBinding.hiddne = true;
					context.linkTargetInputBinding.hidden = true;
					A3Maker.getSelectedComponent().data.linkType = ev.value;
					A3Maker.getSelectedComponent().data.linkTarget = "";
					A3Maker.getSelectedComponent().data.position = ""; 
					
					var linkTargetImes = [{text : "선택", value : ""}];
					switch (ev.value){
						case "PageLink":
							context.linkTargetSelectBinding.hidden = false;
							context.linkTargetInputBinding.hidden = true;
							
							A3Maker.project.pages.forEach(page => linkTargetImes.push({text : page.data.name, value : page.data.id}));
							context.linkTargetSelectBinding.options = linkTargetImes;
							if (ev['init'] && ev['init'] == true){
								context.linkTargetSelectBinding.value = this.PARAMS['position'];
							} else {
								context.linkTargetSelectBinding.value = '';
							}
							break; 
						case "Popup":
							context.linkTargetSelectBinding.hidden = false;
							context.linkTargetInputBinding.hidden = true;

							var selectedPage = A3Maker.getSelectedPage();
							var popups = selectedPage.findComponents("popup");
							popups.forEach(popup => linkTargetImes.push({text : popup.data.name, value : popup.data.id}));
							context.linkTargetSelectBinding.options = linkTargetImes;

							if (ev['init'] && ev['init'] ==true){
								context.linkTargetSelectBinding.value = this.PARAMS['position'];
							} else {
								context.linkTargetSelectBinding.value = '';
							}
							
							break;
						case "Web":
						case "Phonenumber":
						case "YouTube":
						case "MallinMall":
						case "Email":
						case "Media":
						case "Map":
							context.linkTargetSelectBinding.hidden = true;
							context.linkTargetInputBinding.hidden = false;
							//.log(context.linkTargetInputBinding)
							if (ev['init'] && ev['init'] == true){
								context.linkTargetInputBinding.value = this.PARAMS['position'];
							} else {
								this.PARAMS['position'] = '';
								context.linkTargetInputBinding.refresh();
							}
							break;

					}

		
				});
				
				this.linkTargetSelectBinding = this.linkFolder.addBlade({view: 'list', label: 'TARGET',
					options: [],
					value : context.PARAMS.linkTarget
				}).on("change", (ev)=> {
					A3Maker.getSelectedComponent().data.linkTarget = ev.value;
					A3Maker.getSelectedComponent().data.position = ev.value;
				});

				this.linkTargetInputBinding =  this.linkFolder.addBinding(this.PARAMS, "linkTarget",{ label : "TARGET"})
				.on('change', (ev)=> {
					A3Maker.getSelectedComponent().data.position = ev.value;
					A3Maker.getSelectedComponent().data.linkTarget = ev.value;
				  });
				
				  //console.log("---------------------------------------");
				  //console.log(this.linkTargetInputBinding);

				  this.linkTargetInputBinding.element.querySelector('input').style.border = "1px solid #999999";
				  this.linkTargetInputBinding.element.querySelector('input').style.color = "#ffffff";
				  this.linkTargetInputBinding.element.querySelector('input').setAttribute('placeholder', 'Eamil,Map, Web,YouTubre etc');				
				  /*링크 타켓의 셀렉트 박스의 내용을 변경하기 위해 LINK TYPE 셀렉트 박스에 강제로 이벤트를 발생 시킴*/
				this.linkTypeSeletBinding.emitter_.emit('change', {value : this.PARAMS['linkType'] ? this.PARAMS['linkType'] : "PageLink", init : true});
				

			}

			if (propertyType == "text"){
				this.textFolder = this.pane.addFolder({title : "TEXT SETTING"});
				this.fontListBinding =  fontList = this.textFolder.addBlade({view: 'list', label: 'FONT',
					options: [
						{text: '굴림', value: '굴림, Gulim, Arial, sans-serif'},
						{text: '돋음', value: '돋음, Dotum, Baekmuk Dotum, Undotum, Apple Gothic, Latin font, sans-serif'},
						{text: '바탕', value: '바탕, Dotum, Baekmuk Dotum, Undotum, Apple Gothic, Latin font, sans-serif'},
						{text: 'Open Sans', value: 'Open Sans'},
						{text: 'Open Sans bold', value: 'pen Sans|700'},
						{text: 'Arial', value: 'Arial'},
						{text: 'Verdana', value: 'Verdana'},
						{text: 'Impact', value: 'Impact'},
						{text: 'Comic Sans MS', value: 'Comic Sans MS'},
						{text: 'Amatic SC', value: 'Amatic SCs'},
						{text: 'Special Elite', value: 'Open Sans'},
						{text: 'Chelsea Market', value: 'Chelsea Market'},
						{text: 'Comfortaa', value: 'Comfortaa'},
						{text: 'Comfortaa bold', value: 'Comfortaa|7'},
						{text: 'Monoton', value: 'Monoton'},
						{text: 'Shrikhand', value: 'Shrikhand'},
						{text: 'Abril Fatface', value: 'Abril Fatface'},
						{text: 'Anton', value: 'Anton'},
						{text: 'Gloria Hallelujah', value: 'Gloria Hallelujah'},
						{text: 'Poiret One', value: 'Poiret One'},
						{text: 'Permanent Marker', value: 'Permanent Marker'},
						{text: 'Philosopher', value: 'Philosopher'},
						{text: 'Bevan', value: 'Bevan'},
						{text: 'Nanum Pen Script', value: 'Nanum Pen Script'},
						{text: 'Nanum Brush Script', value: 'Nanum Brush Script'},
						{text: 'Nanum Gothic', value: 'Anton'},
						{text: 'Malgun Gothic', value: 'Malgun Gothic'},
						{text: 'Nanum Myeongjo', value: 'Nanum Myeongjo'},
						{text: 'Jeju Gothic', value: 'Jeju Gothic'}
					],
					value: this.PARAMS['font-family']
				})
				.on("change", function(ev){
					var selectedComponent = A3Maker.getSelectedComponent();
					if (selectedComponent.data.type == "text") {
						selectedComponent.setData({'font-family': ev.value});
					}
				});
	  
				/*font list 옵션의 폰트 스트일을 변경해줌*/ 
				 $(fontList.element).find('option').map (function() {
					var value = this.value;
					if (value.indexOf("|") != -1) {
						$(this).css('font-family',$(this).val().split("|")[0]);
						$(this).css('font-weight',$(this).val().split("|")[1]);
					}else {
						$(this).css('font-family',$(this).val());
					}
				});
			
	
				this.fontSizeBinding = this.textFolder.addBlade({view: 'list', label: 'FONT SIZE',
					options: [
						{text: '9pt', value: '9pt'},{text: '10pt', value: '10pt'},{text: '11pt', value: '11pt'},
						{text: '12pt', value: '12pt'},{text: '13pt', value: '13pt'},{text: '14pt', value: '14pt'},
						{text: '15pt', value: '15pt'},{text: '16pt', value: '16pt'},{text: '17pt', value: '17pt'},
						{text: '18pt', value: '18pt'},{text: '19pt', value: '19pt'},{text: '20pt', value: '20pt'},
						{text: '21pt', value: '21pt'},{text: '22pt', value: '22pt'},{text: '23pt', value: '23pt'},
						{text: '24pt', value: '9pt'},{text: '25pt', value: '25pt'},{text: '26pt', value: '9pt'},{text: '27pt', value: '27pt'},
						{text: '28pt', value: '9pt'},{text: '29pt', value: '29pt'},{text: '29pt', value: '29pt'},{text: '30pt', value: '30pt'},
						{text: '31pt', value: '31pt'},{text: '32pt', value: '32pt'},{text: '33pt', value: '33pt'},
						{text: '34pt', value: '34pt'},{text: '35pt', value: '35pt'},{text: '36pt', value: '37pt'},
						{text: '38pt', value: '39pt'},{text: '40pt', value: '40pt'},{text: '40pt', value: '40pt'},
						
						],
						value: this.PARAMS['font-size']
					}).on("change",function(ev){
						var selectedComponent = A3Maker.getSelectedComponent();
						if (selectedComponent.data.type == "text") {
							selectedComponent.setData({'font-size' :  ev.value});
						}
					})
	
				this.fontWeigthBinding = this.textFolder.addBinding(this.PARAMS, 'font-weight', {
					step: 100,
					min: 100,
					max: 1000,
					label : "FONT WEIGHT"
					}).on("change", function(ev){
						var selectedComponent = A3Maker.getSelectedComponent();
						if (selectedComponent.data.type == "text") {
							selectedComponent.setData({'font-weight':   ev.value});
						}
					});
	
				this.fontColorBinding  = this.textFolder.addBinding(this.PARAMS, "color",{label : "FONT COLOR"})
					.on('change', (ev)=> {
						var selectedComponent = A3Maker.getSelectedComponent();
						context.updateData({ color : ev.value});
						
					});
				this.textBinding = this.textFolder.addBinding(this.PARAMS, 'text', {
						view: 'textarea',
						rows: 5,
						multiline: true,
						placeholder: 'Type here...',
						label : "TEXT"
					}).on('change', (ev) => {
						var selectedComponent = A3Maker.getSelectedComponent();
						context.updateData({text : ev.value});
					}
				);

			}

			  
			if (propertyType == 'order'){
				this.orderFolder = this.pane.addFolder({title : "ORDER SETTING"});
				this.orderInfoLoadButtonBinding  = this.orderFolder.addButton({title : "LOAD MENU INFO"})
					.on("click", (ev)=> A3Maker.orderWindow.show(A3Maker.project.storeCode))
				this.storeCodeBinding = this.orderFolder.addBinding(this.PARAMS, "storeCode", {label : "STORE CODE",readonly: true});
				this.menuGroupNameBinding  = this.orderFolder.addBinding(this.PARAMS, "menuGroupName",{label : "MENU GROUP",readonly: true});
				this.menuCodeBinding = this.orderFolder.addBinding(this.PARAMS, "menuCode", {label : "MENU CODE", readonly: true});
				this.menuNameBinding = this.orderFolder.addBinding(this.PARAMS, "menuName", {label : "MENU NAME",readonly: true});
				this.menuDesBinding = this.orderFolder.addBinding(this.PARAMS, "menuDes", {label : "MENU DES",readonly: true});
				this.menuPriceBinding = this.orderFolder.addBinding(this.PARAMS, "menuPrice", {label : "MENU PRICE",readonly: true, format: (number) => parseInt(number).toLocaleString() +' 원'});
				this.menuStockBinding = this.orderFolder.addBinding(this.PARAMS, "menuStock", {label : "MENU STOCK",readonly: true, format: (number) => parseInt(number).toLocaleString()  +' 개'});
				this.menuDisountBinding = this.orderFolder.addBinding(this.PARAMS, "menuDiscount", {label : "DISCOUNT",readonly: true, format: (number) => parseInt(number).toLocaleString()  +' 원'});
			}
			
			if (propertyType == 'panseo'){
				this.canvasFolder = this.pane.addFolder({title : "CANVAS SETTING"});
				var attention = this.canvasFolder.addBlade({
					view: 'text',
					parse: (v) => String(v),
					value: this.PARAMS.attention,
					readonly : true
				  });
				 
				  attention.element.style.color = "#cccccc";
				  attention.element.style.border = "1px solid #cccccc";
				  attention.element.style.fontSize = "10px";
				  
				  //attention.element.classList.add("flowing-text")

				//this.strokeStyleBinding = this.canvasFolder.addBinding(this.PARAMS, "attention" , {label : "주의"});
			
				this.strokeStyleBinding = this.canvasFolder.addBinding(this.PARAMS, "strokeStyle" , {label : 'LINE COLOR'})
				  .on("change", (ev)=>{
					if (A3Maker.getSelectedPage()) {
						if (A3Maker.getSelectedComponent() && A3Maker.getSelectedComponent().data.type == "panseo") {
							A3Maker.getSelectedComponent().setData({strokeStyle : ev.value });
						} else {
							alert("옳바른 사용법이 아닙니다. 먼저 판서컴포넌를 선택한 후 사용해주세요")
						}
					}
				  })
				this.fillStyleBinding = this.canvasFolder.addBinding(this.PARAMS, "fillStyle", {label : "FILL COLOR"})
				 .on("change", (ev)=>{
					if (A3Maker.getSelectedPage()) {
						if (A3Maker.getSelectedComponent() && A3Maker.getSelectedComponent().data.type == "panseo") {
							A3Maker.getSelectedComponent().setData({fillStyle : ev.value });
						} else {
							alert("옳바른 사용법이 아닙니다. 먼저 판서컴포넌를 선택한 후 사용해주세요")
						}
					}
				 });

				this.lineWidthBinding = this.canvasFolder.addBlade({
					view: 'slider',
					label: 'LINE WIDTH',
					min: 1,
					max: 100,
					format : (number) => number.toFixed(0),
					value: this.PARAMS['lineWidth'] ? this.PARAMS['lineWidth'] : 8
					}
				).on("change", (ev)=>context.updateData({lineWidth : ev.value}));	

				this.lineCapBinding =  this.canvasFolder.addBlade({
					view: 'list', 
					label: 'LINE CAP',
					options: [
						{text: 'DEFAULT', value: 'default'},
						{text: 'BUTT', value: 'butt'},
						{text: 'ROUND', value: 'round'},
						{text: 'SQUARE', value: 'square'},
					],
					value: this.PARAMS['lineCap']? this.PARAMS['lineCap'] : "default"
					}
				).on('change', (ev)=> {
					var selectedComponent = A3Maker.getSelectedComponent();
					if (selectedComponent.data.type == "panseo") {
						selectedComponent.setData({'lineCap': ev.value});
					}
				});
				

				this.lineJoinBinding = this.canvasFolder.addBlade({
					view: 'list', 
					label: 'LINE JOIN',
					options: [
						{text: 'DEFAULT', value: 'default'},
						{text: 'VEVEL', value: 'vevel'},
						{text: 'ROUND', value: 'round'},
						{text: 'MITER', value: 'miter'},
						],
						value: this.PARAMS['lineJoin']? this.PARAMS['lineJoin'] : "default"
					}
				).on('change', (ev)=> {
					var selectedComponent = A3Maker.getSelectedComponent();
					if (selectedComponent.data.type == "panseo") {
						selectedComponent.setData({'lineJoin': ev.value});
					}
				});
	
				this.canvasActionButtonBinding = this.canvasFolder.addBlade({
					view: 'buttongrid',
					size: [2, 2],
					cells: (x, y) => ({
						title: [
						['START DRAW', 'START ERASE'],
						['RESET DRAW','CLIPPING'],
						][y][x],
					})
				}).on('click' ,(ev) => {
				//버튼의 문자열을 가져온 후 버튼의 타이틀을 변경함.	 
					//console.log(ev.cell.title);
					var selectedComponent = A3Maker.getSelectedComponent();
					if (selectedComponent.data.type != "panseo") {
						alert("잘못된 사용입니다. 먼저 판서 컴포넌트를 선택해주세요");
						return;
					}

					switch(ev.cell.title){
						case "START DRAW":
							var isErasing = selectedComponent.data.isErasing;
							if (isErasing){
								alert("현재 지우개가 진행 중입니다. 지우기 중지를 한 후 실행해주세요");
								return;
							}
							//var isDrawing = !selectedComponent.data.isDrawing;
							//var drawingText = isDrawing ? 'STOP DRAW' : "START DRAW";
							//var iTag = isDrawing? '<i style = "font-size: 13px;color: red" class="fa fa-ban" aria-hidden="true"></i>' : '<i style = "font-size: 13px" class="fa fa-paint-brush"></i>';
							//var iTag = isDrawing? '<i style = "font-size: 13px;color: red" class="fa fa-ban" aria-hidden="true"></i>' : '<i style = "font-size: 13px" class="fa fa-paint-brush"></i>';
							
							selectedComponent.setData({isDrawing : true});
							ev.cell.title = "STOP DRAW";
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.START_DRAW);
							break;
						case "STOP DRAW":
							ev.cell.title = "START DRAW"
							selectedComponent.setData({isDrawing : false});
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.STOP_DRAW);
							break;
						case "START ERASE":
							isDrawing = selectedComponent.data.isDrawing;
							if (isDrawing){
								alert("현재 그리가가 진행 중입니다. 그리기 중지를 한 후 실행해주세요");
								return;
							}

							//var isErasing = !selectedComponent.data.isErasing;
							//var erasingText = isErasing ? 'STOP ERASE' : "START ERASE";
							//var iTag = isErasing? '<i style = "font-size: 13px;color: red" class="fa fa-ban" aria-hidden="true"></i>' : '<i style = "font-size: 13px" class="fa fa-eraser" aria-hidden="true"></i>';
							
							selectedComponent.setData({isErasing : true});
							ev.cell.title = "STOP ERASE";
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.START_ERASE_DRAW);
							break;
						case "STOP ERASE":
							ev.cell.title = "START ERASE"
							selectedComponent.setData({isErasing : false});
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.STOP_ERASE_DRAW);
							break;
						case "RESET DRAW":
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.CLEAR_DRAW);
							break;
						case "CLIPPING":
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.CLIP_DRAW);
							break;
					
					}
					
				});
				
	
				this.canvasDownButtonBinding = this.canvasFolder.addButton({title : "DOWNLOAD DRAWING"})
					.on('click', (ev)=>selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.DOWN_DRAW));

			}

			if(propertyType == "camera"){
				if (!A3Maker.getSelectedComponent()['VideoRecorder']){
					A3Maker.getSelectedComponent().VideoRecorder = new VideoRecorder(A3Maker.getSelectedComponent());
				}
				const recorder = A3Maker.getSelectedComponent().VideoRecorder;
				
				

				this.recorderFolder = this.pane.addFolder({title : "RECORDER SETTING"});
				this.audioSelect = this.recorderFolder.addBinding(this.PARAMS, 'useAudio',{label : "AUDIO"}).on('change', (ev)=> {})
					.on("change", (ev)=> context.updateData({"useAudio" : ev.value}))
				
				this.micVulumnBinding = this.recorderFolder.addBinding(this.PARAMS, 'micVolume', {
					step: 1,
					min: 0,
					max: 100,
					label : "MIC VOLUME"
					}).on('change', (ev)=> context.updateData({"micVolume" : ev.value}));

				this.customSeettingfolder = this.recorderFolder.addFolder({title : "RESOLUTION CUSTOM"});
				this.customWidthBinding = this.customSeettingfolder.addBinding(this.PARAMS, 'width', {
					step: 1,
					min: A3Maker.config.MIN_COMPONENT_SIZE,
					max: A3Maker.project.width,
					label : "WIDTH"
				}).on('change', (ev)=> {
					this.setPosition(this.PARAMS.left, this.PARAMS.top, ev.value, this.PARAMS.height);
					context.updatePositionAndWidth();
				});
				
				this.customHeightBinding = this.customSeettingfolder.addBinding(this.PARAMS, 'height', {
					step: 1,
					min: A3Maker.config.MIN_COMPONENT_SIZE ,
					max: A3Maker.project.height,
					label : "HEIGHT"
				}).on('change', (ev)=> {
					this.setPosition(this.PARAMS.left, this.PARAMS.top, this.PARAMS.width, ev.value);
					context.updatePositionAndWidth();
				});
				
				this.PARAMS.resolutionType == "CUSTOM" ? 
					this.customSeettingfolder.hidden = false : this.customSeettingfolder.hidden = true  
				
				this.resolutonTypeBinding  =  this.recorderFolder.addBlade({
					view: 'list', 
					label: 'RESOLUTION',
					options: [
						{text: 'VGA(640X480)', value: 'VGA'},
						{text: 'QVGA(320X240)', value: 'QVGA'},
						{text: 'CUSTOM', value: 'CUSTOM'}
						],
						value :this.PARAMS.resolutionType
					}).on('change', (ev)=>{
						switch (ev.value){
							case "CUSTOM":
								this.customSeettingfolder.hidden = false;
								this.updateData({"resolutionType" : ev.value});
								return;
							case "VGA":
								this.PARAMS.width = 640;
								this.PARAMS.height = 480;
								break;
							case "QVGA":
								context.PARAMS.width = 320;
								context.PARAMS.height = 240;
								break;
						}

						this.customSeettingfolder.hidden = true;
						this.setPosition(this.PARAMS.left, this.PARAMS.top, this.PARAMS.width, this.PARAMS.height);
						this.updateData({"resolutionType" : ev.value});
						this.updatePositionAndWidth();
					});

				this.cameraTypeBinding  =  this.recorderFolder.addBlade({
					view: 'list', 
					label: 'CAMERA',
					options: [
						{text: 'FRONT', value: 'user'},
						{text: 'REAR', value: 'environment'}
						],
						value :this.PARAMS.facingMode
					}).on('change', (ev)=> context.updateData({"facingMode" : ev.value}));
				
				var videoStatus = this.PARAMS.videoStatus;
				this.recorderActionButtonBinding = this.recorderFolder.addBlade({
					view: 'buttongrid',
					size: [2, 3],
					cells: (x, y) => ({
						title: [
						[videoStatus.isPlaying ? 'STOP VIDEO' : 'START VIDEO' , videoStatus.isPausing ? 'RESUME' : 'PAUSE'],
						[videoStatus.isRecording ? 'STOP RECORD': 'START RECORD', videoStatus.isPreviewing ?'STOP PREVIEW': 'PREVIEW'],
						['DATA RESET', 'DOWNLOAD VIDEO']
						][y][x],
					})
				}).on("click", (ev) =>{
					context.handleRecorderAction(ev,recorder);
				});
				console.log('카메라 버튼 ');
				console.log(this.recorderActionButtonBinding);
				// this.cameraDownButtonBinding = this.recorderFolder.addButton({title : "DOWNLOAD VIDEO"})
				// 	.on('click', (ev)=>{});
			}
			this.pane.refresh();
		},

		handleRecorderAction : function(ev, recorder){
			var targetComponent = A3Maker.getSelectedComponent();
				if (targetComponent.data.type != "camera") {
					alert("잘못된 사용입니다. 먼저 판서 컴포넌트를 선택해주세요");
					return;
				}
			switch(ev.cell.title){
				case "START VIDEO":
					
					var result = recorder.start();
					result.then(value => {
						console.log(value);
						if(!value){
							targetComponent.data.videoStatus.isPlaying = false;
							this.PARAMS.videoStatus.isPlaying = false ;
							ev.cell.title = "START VIDEO"
						}else {
							targetComponent.data.videoStatus.isPlaying = true;
							this.PARAMS.videoStatus.isPlaying = true;
							ev.cell.title = "STOP VIDEO"
						}
					});
					
					break;
				case "STOP VIDEO":
					ev.cell.title = "START VIDEO";
					recorder.stop();
					targetComponent.data.videoStatus.isPlaying = false;
					targetComponent.data.videoStatus.isRecording = false;
					targetComponent.data.videoStatus.isPausing = false;
					targetComponent.data.videoStatus.isPreviewing = false;
					this.PARAMS.videoStatus.isPlaying = false;
					this.PARAMS.videoStatus.isRecording = false;
					this.PARAMS.videoStatus.isPausing = false;
					this.PARAMS.videoStatus.isPreviewing = false;
					this.PARAMS.videoStatus.isPausing = false;
					this.recorderActionButtonBinding.cell(1,0).title = "PAUSE"
					this.recorderActionButtonBinding.cell(0,1).title = "START RECORD"
					this.recorderActionButtonBinding.cell(1,1).title = "PREVIEW"

					break;
				case "PAUSE":
					if (targetComponent.data.videoStatus.isRecording){
						alert("현재 레코딩이 진행중입니다. 레코딩을 중단하고 다시 시도해주세요");
						return;
					}
					ev.cell.title = "RESUME";
					recorder.pause();
					targetComponent.data.videoStatus.isPausing = true;
					this.PARAMS.videoStatus.isPausing = true
					break;
				case "RESUME":
					ev.cell.title = "PAUSE";
					recorder.resume();
					targetComponent.data.videoStatus.isPausing = false;
					this.PARAMS.videoStatus.isPausing = false;
					break;
				
				case "START RECORD":
					if (!targetComponent.data.videoStatus.isPlaying){
						alert("현재 카메라 데이타를 수신중이 아닙니다. START VIDEO를 눌러서 카메라 수신을 시작핮 후 레코딩을 해주세요");
						return;
					}

					if (targetComponent.data.videoStatus.isPausing){
						alert("현재 Pause 상태입니다.. RESUME을 눌러서 카메라 수신을 시작한 후 다시 시도해주세요");
						return;
					}


					ev.cell.title = "STOP RECORD";
					recorder.startRecording();
					targetComponent.data.videoStatus.isRecording = true;
					this.PARAMS.videoStatus.isRecording = true;
					break;;
				case "STOP RECORD":
					ev.cell.title = "START RECORD";
					recorder.stopRecording();
					targetComponent.data.videoStatus.isRecording =false;
					this.PARAMS.videoStatus.isRecording = false;
					break;
				case "PREVIEW":
					var result = recorder.playPreview(); 
					!result ? targetComponent.data.videoStatus.isPreviewing = false : targetComponent.data.videoStatus.isPreviewing = true;
					!result ? this.PARAMS.videoStatus.isPreviewing = false : this.PARAMS.videoStatus.isPreviewing = true;
					ev.cell.title = !result ?  "PREVIEW" :  "STOP PREVIEW";
					break;
				case "STOP PREVIEW":
					ev.cell.title = "PREVIEW";
					recorder.stopPreview();
					targetComponent.data.videoStatus.isPreviewing =false;
					this.PARAMS.videoStatus.isPreviewing = false;
					break;
				
			}	
		},
		changeComponentName: function (options){
			
			if (options.name.length < 1 || options.name == '') {
				alert("컴퍼넌트 이름은 공백이 될 수 없습니다");
				return;
			}
			var selectedPage = A3Maker.getSelectedPage();
			var selectedComponent =  A3Maker.getSelectedComponent();

			if (selectedPage){
				if (selectedComponent) {
					//같은 이름이기 때문에 처리 안함
					if (selectedComponent.data.name == options.name) return;
					
					//같은 이름이 아닐때, 해당 이름의 컴포넌트가  있는 지 검사  	
					if (selectedPage.hasSameNamedComponent(options.name)){
						alert("해당 페이지 안에 중복된 이름의 컴포넌트가 있습니다. 이름을 변경해주세요");
						return;
					}
					
					selectedComponent.setData(options);
					var target_node = A3Maker.controller.$tree.tree('getNodeById', selectedComponent.data.UUID);
					A3Maker.controller.$tree.tree('updateNode', target_node,  options.name);

				} else {
					//같은 이름이기 때문에 처리 안함
					if (selectedPage.data.name.trim() == options.name ) return;

					//같은 이름이 아닐때, 해당 이름의 페이지가  있는 지 검사  
					if (A3Maker.getProject().hasSameNamedPage(options.name)) {
						alert("페이지 이름이 중복됩니다. 중복되지 않은 이름으로 변경해해주세요");
						return;
					}					
					
					// context.$backColorPicker.css('backgroundColor', color);
					selectedPage.setData(options);
					var target_node = A3Maker.controller.$tree.tree('getNodeById', selectedPage.data.UUID);
					A3Maker.controller.$tree.tree('updateNode', target_node,  options.name);
				}
				
			}

		},

		setData: function (property, value) {
			switch (property) {
				case "name":
					this.PARAMS.name = value;
					this.nameBinding.refresh();
					break;
			}
		},

		hideAllLInkPositionElem: function () {
			this.$linkPageSelectDiv.hide();
			this.$linkPopupSelectDiv.hide();
			this.$linkWebText.hide();
			this.$phoneNumberText.hide();
			this.$youTubeText.hide();
			this.$mallInMallText.hide();
			this.$emailText.hide();
			this.$mapDiv.hide();
		},

		resetAllLinkPositionElem: function () {
			this.$linkPageSelect.val('');
			this.$linkPopupSelect.val('');
			this.$linkWebText.val('');
			this.$phoneNumberText.val('');
			this.$youTubeText.val('');
			this.$mallInMallText.val('');
			this.$emailText.val('');
			this.$mapText.val('');
		},

		updateBorder: function (border) {
			var selectedComponent = A3Maker.getSelectedComponent();
			if (selectedComponent) {
				if (selectedComponent.selectStatus) {
					selectedComponent.data.border = border;
					selectedComponent.setBorder(border);
				}
			}
		},

		updatePositionAndWidth: function (options) {
			
			if (!options) {
				options = {};
				options.top = this.PARAMS.top;
				options.left = this.PARAMS.left;
				options.width = this.PARAMS.width;
				options.height = this.PARAMS.height;
			}
			var selectedComponent = A3Maker.getSelectedComponent();
			var selectedComponentHeight = selectedComponent.data.height;

			if (selectedComponent) {
				selectedComponent.updatePositionAndWidth(options.left, options.top, options.width, options.height);
				if (selectedComponent.data.type == 'listrow')
					selectedComponent.updatePositionNextRows(0, height - selectedComponentHeight);
			}
		},

		onChangeComponent: function (event, target) {
			
			var newName = event.target.value.trim();

			if (newName.length < 1 || newName == '') {
				alert("콤퍼넌트 이름은 공백이 될 수 없습니다");
				$(target).focus();
				return;
			}
			var selectedPage = A3Maker.getSelectedPage();
			var selectedComponent =  A3Maker.getSelectedComponent();

			if (selectedPage){
				if (selectedComponent) {
					//같은 이름이기 때문에 처리 안함
					if (selectedComponent.data.name == newName) return;
					
					//같은 이름이 아닐때, 해당 이름의 컴포넌트가   있는 지 검사  	
					if (selectedPage.hasSameNamedComponent(newName)){
						alert("해당 페이지 안에 중복된 이름의 컴포넌트가 있습니다. 이름을 변경해주세요");
						$("#element .field_value_name").focus();
						return;
					}
					
					selectedComponent.setData({name : newName});
					var target_node = A3Maker.controller.$tree.tree('getNodeById', selectedComponent.data.UUID);
					A3Maker.controller.$tree.tree('updateNode', target_node, newName);

				} else {
					//같은 이름이기 때문에 처리 안함
					if (selectedPage.data.name.trim() == newName ) return;

					//같은 이름이 아닐때, 해당 이름의 페이지가  있는 지 검사  
					if (A3Maker.getProject().hasSameNamedPage(newName)) {
						alert("페이지 이름이 중복됩니다. 중복되지않은 이름으로 변경해해주세요");
						$("#element .field_value_name").focus();
						return;
					}					
					
					// context.$backColorPicker.css('backgroundColor', color);
					selectedPage.setData('name',newName );
					var target_node = A3Maker.controller.$tree.tree('getNodeById', selectedPage.data.UUID);
					A3Maker.controller.$tree.tree('updateNode', target_node, newName);
				}
				
			}
		},

		initializeEventHandler: function () {
			var context = this;
			$('#element .field_value_x, #element .field_value_y, #element .field_value_w, #element .field_value_h')
				.focusout(function (e) {
					e.stopPropagation();
					var name = $(".field_value_name").val();
					if (name.trim() ==''){
						alert("이름은 공백이 될 수 없습니다. 이름을 입력해주세요");
						$("#element .field_value_name").focus();
						return;
					}
					context.updatePositionAndWidth($(this).parent().attr("id"));
			});

			$('#element .field_value_x, #element .field_value_y, #element .field_value_w, #element .field_value_h')
				.blur(function (e) {
					e.stopPropagation();
					context.updatePositionAndWidth($(this).parent().attr("id"));
			});

			$('#element .field_value_x, #element .field_value_y, #element .field_value_w, #element .field_value_h')
				.keyup(function(e){
					if (e.which == 13) {
						var name = $("#element .field_value_name").val();
				
						if (name.trim() ==''){
							alert("이름은 공백이 될 수 없습니다. 이름을 입력해주세요");
							$("#element .field_value_name").focus();
							return;
						}
						context.updatePositionAndWidth($(this).parent().attr("id"));
					}
				}
			);

			
			// this.$name.focusout(function(event) {
			// 	context.onChangeComponentName(event, this);
			// });
			this.$name.keyup(function(event) {
				if (event.which != 13)  return;
				context.onChangeComponentName(event, this);
			});

			$('#menuLoading_btn').click(function (e) {
					A3Maker.orderWindow.show(A3Maker.project.storeCode);
				});
			
			this.$fixRatio.click(function (event) {
				var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent.data.type != 'order' && selectedComponent.data.type != 'image' && selectedComponent.data.type != 'link') return;
				var componentX = selectedComponent.data.left;
				var componentY = selectedComponent.data.top;

				var componentWidth = selectedComponent.data.width;
				var componentHeight = selectedComponent.data.height;

				var isFixRatio = $("#element .prop_" + selectedComponent.data.type + " .field_value_owhyn").is(':checked');
				var width = 0;
				var height = 0;

				if (isFixRatio == true) {
					var naturalWidth = $(selectedComponent.originalElementQueryStr).get(0).naturalWidth;
					var naturalHeight = $(selectedComponent.originalElementQueryStr).get(0).naturalHeight;
					if (componentWidth > componentHeight) {
						width = parseInt(componentWidth);
						height = parseInt((naturalHeight * componentWidth) / naturalWidth);
					} else if (componentWidth < componentHeight) {
						width = parseInt((naturalWidth * componentHeight) / naturalHeight);
						height = parseInt(componentHeight);
					} else {
						width = parseInt(componentWidth);
						height = parseInt((naturalHeight * componentWidth) / naturalWidth);
					}
					selectedComponent.setPositionAndWidth(componentX, componentY, width, height);
				}
				A3Maker.getSelectedComponent().data.fixRatio = isFixRatio;
			});

			this.$lineCapSelect.change(function(e){
				var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent.data.type == "panseo") {
					selectedComponent.setData({'lineCap': $(this).val()});
				}
			});

			this.$lineJoinSelect.change(function(e){
				var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent.data.type == "panseo") {
					selectedComponent.setData({'lineJoin': $(this).val()});
				}
			});
		
			this.$fontSelect.change(function (event) {
				var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent.data.type == "text") {
					selectedComponent.setData({'font-family': $(this).val()});
				}
			});

			this.$fontWeightSelect.change(function (event) {
				var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent.data.type == "text") {
					selectedComponent.setData({'font-weight': $(this).val()});
				}
			});

			this.$fontSizeSelect.change(function (event) {
				var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent.data.type == "text") {
					selectedComponent.setData({'font-size' :  $(this).val()});
				}
			});

			this.$textContent.bind('input propertychange', function () {
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.text = $(this).val();
				selectedComponent.originalElement.find('textarea').val($(this).val());
			});

			this.$backColorPicker.ColorPicker({
				onShow: function (colpkr) {

					$(colpkr).fadeIn(500);
					return false;
				},
				onHide: function (colpkr) {
					$(colpkr).fadeOut(500);
					return false;
				},
				onChange: function (hsb, hex, rgb) {
					var color = '#' + hex;
					$("#back_color_container").css("background-color", color);
					if (A3Maker.getSelectedPage()) {
						if (A3Maker.getSelectedComponent()) {
							A3Maker.getSelectedComponent().setData({ "background-color" : color });
						} else {
							var selectedPage = A3Maker.getSelectedPage();
							context.$backColorPicker.css('background-color', color);
							selectedPage.setData("background-color" , color);
							selectedPage.refresh();
						}
					}

				}
			});

			this.$lineColorPicker.ColorPicker({
				onShow: function (colpkr) {

					$(colpkr).fadeIn(500);
					return false;
				},
				onHide: function (colpkr) {
					$(colpkr).fadeOut(500);
					return false;
				},
				onChange: function (hsb, hex, rgb) {
					var color = '#' + hex;
					$("#line_color_container").css("background-color", color);
					if (A3Maker.getSelectedPage()) {
						if (A3Maker.getSelectedComponent() && A3Maker.getSelectedComponent().data.type == "panseo") {
							A3Maker.getSelectedComponent().setData({strokeStyle : color });
						} else {
							alert("옳바른 사용법이 아닙니다. 먼저 판서컴포넌를 선택한 후 사용해주세요")
						}
					}

				}
			});
			this.$fillStylePicker.ColorPicker({
				onShow: function (colpkr) {

					$(colpkr).fadeIn(500);
					return false;
				},
				onHide: function (colpkr) {
					$(colpkr).fadeOut(500);
					return false;
				},
				onChange: function (hsb, hex, rgb) {
					var color = '#' + hex;
					$("#fill_style_container").css("background-color", color);
					if (A3Maker.getSelectedPage()) {
						if (A3Maker.getSelectedComponent() && A3Maker.getSelectedComponent().data.type == "panseo") {
							A3Maker.getSelectedComponent().setData({fillStyle : color });
						} else {
							alert("옳바른 사용법이 아닙니다. 먼저 판서컴포넌를 선택한 후 사용해주세요")
						}
					}

				}
			});

			this.$colorPicker.ColorPicker({
				onShow: function (colpkr) {
					$(colpkr).fadeIn(500);
					return false;
				},
				onHide: function (colpkr) {
					$(colpkr).fadeOut(500);
					return false;
				},
				onChange: function (hsb, hex, rgb) {
					var selectedComponent = A3Maker.getSelectedComponent();
					var color = '#' + hex;
					context.$colorPicker.css('background-color', color);
					context.updateData({"font-color" : color, color : color});
					
				}
			});

			var radiusHandle = $("#radius_handle");
			$("#radius_slider").slider({
				create: function () {
					context.$radiusHandle.text($(this).slider("value"));
				},
				slide: function (event, ui) {
					context.$radiusHandle.text(ui.value);
					context.updateData({"border-radius" : ui.value})
				},
				min: 0,
				max: 50
			});

			var opacityHandle = $("#opacity_handle");
			$("#opacity_slider").slider({
				create: function () {
					context.$opacityHandle.text($(this).slider("value"));
				},
				slide: function (event, ui) {
					context.$opacityHandle.text(ui.value);
					context.updateData({"opacity" : ui.value / 100})
					
				},
				min: 0,
				max: 100
			});

			
			
			$("#linewidth_slider").slider({
				create: function () {
					context.$linewidthHandle.text($(this).slider("value"));
				},

				slide: function (event, ui) {
					context.$linewidthHandle.text(ui.value);
					context.updateData({lineWidth : ui.value});
				}
				,
				min: 1,
				max: 100
			});

			// var borderHandle = $("#border_handle");
			// $("#border_slider").slider({
			// 	create: function () {
			// 		borderHandle.text($(this).slider("value"));
			// 	},
			// 	slide: function (event, ui) {
			// 		borderHandle.text(ui.value);
			// 		context.updateBorder(parseInt(ui.value));
			// 	},
			// 	min: 0,
			// 	max: 50
			// });

			this.$pageTypeSelect.change(function () {
				context.hideAllLInkPositionElem();
				//context.resetAllLinkPositionElem();

				var selectValue = $(this).val();
				var selectedComponent = A3Maker.getSelectedComponent();

				selectedComponent.data.linkType = selectValue;
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('linktype', selectValue);
				switch (selectValue) {
					case "PageLink":
						selectedComponent.data.position = $('#element .field_value_linkpage option:selected').val();
						context.setPageLinkSelect();
						context.$linkPageSelect.val(selectedComponent.position);
						context.$linkPageSelectDiv.show();
						break;
					case "Web":
						selectedComponent.data.position = context.$linkWebText.val();
						context.$linkWebText.show();
						break;
					case "Popup":
						selectedComponent.data.position = $('#element .field_value_linkpopup option:selected').val();
						context.setPopupSelect();
						context.$linkPopupSelect.val(selectedComponent.position);
						context.$linkPopupSelectDiv.show();
						break;
					case "Phonenumber":
						selectedComponent.data.position = context.$phoneNumberText.val();
						context.$phoneNumberText.show();
						break;
					case "YouTube":
						selectedComponent.data.position = context.$youTubeText.val();
						context.$youTubeText.show();
						break;
					case "MallinMall":
						selectedComponent.data.position = context.$mallInMallText.val();
						context.$mallInMallText.show();
						break;
					case "Email":
						selectedComponent.data.position = context.$emailText.val();
						context.$emailText.show();
						break;
					case "Map":
						selectedComponent.data.position = context.$mapText.val();
						context.$mapDiv.show();
						break;
				}
			});
			this.$linkPageSelect.bind('change', function (event) {
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
			});

			this.$linkPopupSelect.bind('change', function (event) {
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
			});

			this.$phoneNumberText.focusout(function (e) {
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
			});

			this.$linkWebText.focusout(function (e) {
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
			});

			this.$phoneNumberText.keydown(function (e) {
				if (e.which == 13) {
					var selectedComponent = A3Maker.getSelectedComponent();
					selectedComponent.data.position = $(this).val();
					$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
				}
			});

			this.$linkWebText.keydown(function (e) {
				if (e.which == 13) {
					var selectedComponent = A3Maker.getSelectedComponent();
					selectedComponent.data.position = $(this).val();
					$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
				}
			});

			this.$youTubeText.bind('focusout keydown', function (event) {
				if (event.type == 'keydown') {
					if (event.which != 13)
						return;
				}
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
				//console.log("position : " + selectedComponent.data.position);
			});

			this.$emailText.bind('focusout keydown', function (event) {
				if (event.type == 'keydown') {
					if (event.which != 13)
						return;
				}
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
				//console.log("position : " + selectedComponent.data.position);
			});

			this.$mallInMallText.bind('focusout keydown', function (event) {
				if (event.type == 'keydown') {
					if (event.which != 13)
						return;
				}
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', $(this).val());
				//console.log("position : " + selectedComponent.data.position);
			});

			this.$mapText.bind('focusout keydown', function (event) {
				if (event.type == 'keydown') {
					if (event.which != 13)
						return;
				}
				var selectedComponent = A3Maker.getSelectedComponent();
				selectedComponent.data.position = $(this).val();
				$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', selectedComponent.data.position);
			});

			$('.field_value_linkmap_lon').bind(
				'focusout keydown',
				function (event) {
					if (event.type == 'keydown') {
						if (event.which != 13)
							return;
					}
					var selectedComponent = A3Maker.getSelectedComponent();
					selectedComponent.data.position = $('.field_value_linkmap_lat').val()
						+ "," + $(this).val();
					$(selectedComponent.externalWrapperQueryStr + ' .component-link').attr('position', selectedComponent.position);
					//console.log("position : " + selectedComponent.data.position);
				});


			this.$uploadFake.click(function (event) {
				context.$uploadImgSelector.trigger('click');
			});


			this.$uploadImgSelector.change(function (event) {

				if (A3Maker.mode == "test") {
					var file = event.target.files[0];

					$('#abmsProjectNo').val(A3Maker.project.projectSeq); // project_seq insert
					const reader = new FileReader();
					reader.readAsDataURL(file);
					reader.onload = function (e) {

						if (A3Maker.getSelectedPage()) {
							if (A3Maker.getSelectedComponent()) {
								A3Maker.getSelectedComponent().setData({ 'background-image': e.target.result });
							} else {
								A3Maker.getSelectedPage().data['background-image'] = e.target.result;
								A3Maker.getSelectedPage().$pageHolderSelector.css('background-image', 'url(' + e.target.result + ')')
							}
						}

						context.$uploadImgSelector.val("");
						context.isUploading = false;
						A3Maker.sidebar.updateSideThumbnailView();
					}
				} else {
					$('#abmsProjectNo').val(A3Maker.project.projectSeq); // project_seq insert
					context.$uploadImageFrm.submit();
				}

			});

			this.$uploadImageFrm.ajaxForm({
				beforeSubmit: function (data, frm, opt) {
					if ($('.ajax_center_loading').length < 1) {
						context.$centerLoadIngProgress = $(A3Maker.ajaxCenterLoadingHTML);
						$('body').prepend(context.$centerLoadIngProgress);
					}
					context.isUploading = true;
					return true;
				},

				success: function (responseText, statusText) {
					context.$centerLoadIngProgress.hide();
					context.$centerLoadIngProgress.remove();
					//console.log(responseText)
					var res = responseText;
					if (res.resultCode != 100) {
						setTimeout(function () { alert(res.messageText); }, 30)
						return;
					}
					var uploadImgUrl = res.data;
					//console.log("업로드 이미지 경로");
					//console.log(uploadImgUrl);
					if (A3Maker.getSelectedPage()) {
						if (A3Maker.getSelectedComponent()) {
							A3Maker.getSelectedComponent().setData({ 'background-image': uploadImgUrl });
						} else {
							A3Maker.getSelectedPage().data['background-image'] = uploadImgUrl;
							A3Maker.getSelectedPage().$pageHolderSelector.css('background-image', 'url(' + uploadImgUrl + ')')
						}
					}

					context.$uploadImgSelector.val("");
					context.isUploading = false;
					A3Maker.sidebar.updateSideThumbnailView();
				},

				error: function (xhr, options, error) {
					context.$centerLoadIngProgress.hide();
					context.$centerLoadIngProgress.remove();
					context.isUploading = false;
					var errorMessage = '업로드 에러  : ' + error;
					alert(errorMessage);
				}
			});

			$(".panseo_action").click(function(e){
				
				var action = $(this).data('action');
				var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent.data.type != "panseo") {
					alert("잘못된 사용입니다. 먼저 판서 컴포넌트를 선택해주세요");
					return;
				}

				switch(action){
					case A3Maker.config.CANVAS_MODE.START_DRAW:
						var isErasing = selectedComponent.data.isErasing;
						if (isErasing){
							alert("현재 지우개가 진행 중입니다. 지우기 중지를 한 후 실행해주세요");
							return;
						}
						var isDrawing = !selectedComponent.data.isDrawing;
						var drawingText = isDrawing ? '<span style = "color : red">그리기 중지</span>' : "그리기 시작";
						var iTag = isDrawing? '<i style = "font-size: 13px;color: red" class="fa fa-ban" aria-hidden="true"></i>' : '<i style = "font-size: 13px" class="fa fa-paint-brush"></i>';

						selectedComponent.setData({isDrawing : isDrawing});
						
						$(this).html('')
						$(this).html(iTag + drawingText);
						if (selectedComponent.data.isDrawing){
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.START_DRAW);
						}else {
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.STOP_DRAW);
						}
						break;
						
					case A3Maker.config.CANVAS_MODE.CLIP_DRAW:
						selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.CLIP_DRAW);
						break;
						
					case A3Maker.config.CANVAS_MODE.CLEAR_DRAW:
						selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.CLEAR_DRAW);
						break;
					case A3Maker.config.CANVAS_MODE.DOWN_DRAW:
						selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.DOWN_DRAW);
						break;
							
					case A3Maker.config.CANVAS_MODE.ERASE_DRAW:
						isDrawing = selectedComponent.data.isDrawing;
						if (isDrawing){
							alert("현재 그리가가 진행 중입니다. 그리기 중지를 한 후 실행해주세요");
								return;
						}

						var isErasing = !selectedComponent.data.isErasing;
						var erasingText = isErasing ? '<span style = "color : red">지우개 중지</span>' : "지우개 시작";
						var iTag = isErasing? '<i style = "font-size: 13px;color: red" class="fa fa-ban" aria-hidden="true"></i>' : '<i style = "font-size: 13px" class="fa fa-eraser" aria-hidden="true"></i>';
						
						selectedComponent.setData({isErasing : isErasing});

						$(this).html('')
						$(this).html(iTag + erasingText);
						if (selectedComponent.data.isErasing){
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.START_ERASE_DRAW);
						}else {
							selectedComponent.settingCanvas(A3Maker.config.CANVAS_MODE.STOP_ERASE_DRAW);
						}
						break;
				}
			});

			$('.extra-action').click(function () {
				var action = $(this).data('action');

				switch (action) {
					case "reset image":
						
						var target = A3Maker.getSelectedComponent() ? A3Maker.getSelectedComponent() : A3Maker.getSelectedPage();
						
						if (!target.data['background-image'] || target.data['background-image'].trim().length < 1 || target.data['background-image'].trim() == target.defaultsrc || target.data['background-image'] == "") {
							alert("설정되어 있는 이미지가 없습니다");
							return;
						}
						if (confirm("설정된 이미지가 초기화 됩니다.") == true) {
							if (A3Maker.getSelectedPage()) {
								if (A3Maker.getSelectedComponent()) {
									A3Maker.getSelectedComponent().setData({'background-image': '', 'border-radius' : 0});
								} else {
									A3Maker.getSelectedPage().setData('background-image', '');
								}
							}
							A3Maker.sidebar.updateSideThumbnailView();
						}
						break;
					case "clipart":
						A3Maker.clipArt.show();
						break;
					case "upload_image":
						context.$uploadImgSelector.trigger('click');
						break;
					case "download_image":
						var target = A3Maker.getSelectedComponent() ? A3Maker.getSelectedComponent() : A3Maker.getSelectedPage();
						if (target.data['background-image'] != '' && target.data['background-image'].startsWith('/')) {
							if (confirm("이미지를 다운로드 하시겠습니까")) {
								//console.log("다운로드 파일 : " + target.src);
								if ($("#ifrm").length > 0) $("#ifrm").remove();
								var url = '/abms/A3Maker/downloadResource?projectSeq=' + A3Maker.project.projectSeq + "&fileName=" + target.src;
								var html = "<iframe style ='width:0px;height:0px' id='ifrm' src='" + url + "'>";
								$("body").append(html);
							}
						} else {
							if (target.data['background-image'].startsWith('data:image')) {
								var fileExt = target.data['background-image'].split(";")[0].split("/")[1];

								if (fileExt == "jpeg") {
									fileExt = "jpg";
								}

								var fileName = fileName =
									A3Maker.project.projectName.replace(/ /g, '_') + '_' + $.datepicker.formatDate('yymmdd', new Date()) + "." + fileExt;
								//console.log(fileName);
								// if (target.data['background-image'].indexOf('data:image/jpg') >=0 || target.data['background-image'].indexOf('data:image/jpeg') >=0 ){
								// 	fileName = A3Maker.project.projectName.replace(/ /g, '_') +'_'+ $.datepicker.formatDate('yymmdd', new Date()) + '.jpg';
								// 	//console.log(fileName)
								// }

								// if (target.data['background-image'].indexOf('data:image/png') >=0 ){
								// 	fileName = A3Maker.project.projectName.replace(/ /g, '_') +'_'+ $.datepicker.formatDate('yymmdd', new Date()) + '.png';
								// 	//console.log(fileName)
								// }
								var downLoadTag = document.createElement("a");
								downLoadTag.setAttribute('href', target.data['background-image']);
								downLoadTag.setAttribute('download', fileName);
								downLoadTag.setAttribute('id', "tempDownLoadTag");
								document.body.appendChild(downLoadTag);
								downLoadTag.click();
								document.body.removeChild(downLoadTag);
							} else {
								alert("다운로드할 이미지가 없습니다");
							}
						}
						break;
				}
			});
		},


		updateData : function(data){
			var selectedComponent = A3Maker.getSelectedComponent();
				if (selectedComponent) {
					selectedComponent.setData(data);
				}
		},

		setPageLinkSelect: function () {
			var context = this;
			this.$linkPageSelect.find("option").each(function () {
				if (this.value) {
					$(this).remove();
				}
			});

			$.each(A3Maker.project.pages, function (index, page) {
				context.appendSelectItem(page);
			});
		},

		setPopupSelect: function () {
			var context = this;
			this.$linkPopupSelect.find("option").each(function () {
				if (this.value != "") {
					$(this).remove();
				}
			});

			var selectedPage = A3Maker.getSelectedPage();
			var popups = selectedPage.findComponents('popup');
			$.each(popups, function (index, popup) {
				context.appendSelectItem(popup);
			});
		},

		setPosition: function (x, y, w, h) {
			this.PARAMS.top = y;
			this.PARAMS.autoEvent = true;
			this.PARAMS.left = x;
			this.PARAMS.width = w;
			this.PARAMS.height = h;

			//tweakpane 은 값을 넣는 즉시 change 이벤트를 발생시키기 때문에 무한 루프에 걸릴 수 있음 
			//그래서 해당 엘리먼트에 직접 값을 넣어준다.
			//외부에서 프로그램으로 값을 변경할 때도 무조건 change 이벤트가ㅣ 발생.. 이걸 막을 수가 없음
			this.xBinding.element.querySelector('input').value = x;  
			this.yBinding.element.querySelector('input').value = y;  
			this.widthBinding.element.querySelector('input').value = w;  
			this.heigthBinding.element.querySelector('input').value = h; 

			var selectedComponet = A3Maker.getSelectedComponent();
			//선택된 컴포넌트가 Camera 이고, 리졸류션 모드가 CUSTOM 일 때 해당 항목을 업데이트 해준다.
			if (selectedComponet && selectedComponet.data.type == "camera" && selectedComponet.data.resolutionType == "CUSTOM"){
				//this.customHeightBinding.controller.value.value_.setRawValue(w);
				this.customWidthBinding.element.querySelector('input').value = w;
				this.customHeightBinding.element.querySelector('input').value = h;
				
				var deltaX = A3Maker.project.width - A3Maker.config.MIN_COMPONENT_SIZE;
				var deltaY = A3Maker.project.height - A3Maker.config.MIN_COMPONENT_SIZE;
				
				this.customWidthBinding.element.querySelector('.tp-sldv_k').style.width = ((w-A3Maker.config.MIN_COMPONENT_SIZE) * 100 / deltaX) + "%";
				this.customHeightBinding.element.querySelector('.tp-sldv_k').style.width = ((h-A3Maker.config.MIN_COMPONENT_SIZE) * 100 / deltaY) + "%";
			}
		},

		setInitInfo: function (options) {
			// this.extract(options);
			// this.setPosition(options.x, options.y, options.width, options.height);
			// this.fixRatioBinding.refresh();
			// if (this.nameBinding){
			// 	this.setData('name', options['name']);
			// 	this.fixRatioBinding.refresh();
			// }
		},

		movePropertyWindow: function (x, y) {
			//this.positionX = x;
			//this.positionY = y;
			//this.propertyWindow.css('right', this.positionX);
			//this.propertyWindow.css('top', this.positionY);
		},

		setPropertyWindowPos: function (right, top) {
			this.positionX = right;
			this.positionY = top;
			this.propertyWindow.css('right', this.positionX);
			this.propertyWindow.css('top', this.positionY);
		},

		appendSelectItem: function (item) {
			switch (item.data.type) {
				case "page": this.$linkPageSelect.append("<option value ='" + item.data.id + "'>" + item.data.name + "</option>"); break;
				case "popup": this.$linkPopupSelect.append("<option value ='" + item.data.id + "'>" + item.data.name + "</option>"); break;
			}
		},

		deleteSelectItem: function (item) {
			switch (item.data.type) {
				case "page":
					this.$linkPageSelect.find('option').each(function () {
						if (this.value == item.data.fullName) {
							$(this).remove();
						}
					});
					break;
				case "popup":
					this.$linkPopupSelect.find('option').each(function () {
						if (this.value == item.data.fullName) {
							$(this).remove();
						}
					});
					break;
			}
		},

		resetPropertyWindow: function (propertyType) {
			var selectedComponent = A3Maker.getSelectedComponent();
			selectedComponent = selectedComponent ? selectedComponent : A3Maker.getSelectedPage();
			//이미지가 필요없는 컴포넌트의 경우 이미지 업로드 버튼 및 관련 폼 제거 
			if (propertyType == 'image' || propertyType == 'link' || propertyType == 'order') this.$uploadFake.show();
			$('.field_value_x, .field_value_y, .field_value_w').removeAttr("disabled");

			//Radius Slider set
			if (typeof selectedComponent.data["border-radius"] !== "undefined") {
				$("#radius_slider").slider({ value: selectedComponent.data['border-radius'] });
				$("#radius_handle").text(selectedComponent.data['border-radius'])
			} else {
				$("#radius_slider").slider({ value: 0 })
				$("#radius_handle").text(0)
			}

			//opacity slider
			if (typeof selectedComponent.data.opacity !== "undefined") {
				$("#opacity_slider").slider({ value: parseInt(selectedComponent.data.opacity * 100 )});
				$("#opacity_handle").text(parseInt(selectedComponent.data.opacity * 100 ))
			} else {
				$("#opacity_slider").slider({ value: 0 })
				$("#opacity_handle").text(0)
			}

			if (typeof selectedComponent.data.opacity !== "undefined") {
				$("#opacity_slider").slider({ value: parseInt(selectedComponent.data.opacity * 100 )});
				$("#opacity_handle").text(parseInt(selectedComponent.data.opacity * 100 ))
			} else {
				$("#opacity_slider").slider({ value: 0 })
				$("#opacity_handle").text(0)
			}

			//linewith slider
			if (typeof selectedComponent.data.lineWidth !== "undefined") {
				$("#linewidth_slider").slider({ value: selectedComponent.data.lineWidth });
				$("#linewidth_handle").text(selectedComponent.data.lineWidth)
			} else {
				$("#linewidth_slider").slider({ value: 0 })
				$("#linewidth_handle").text(0)
			}

			this.$textContent.val('');
			this.$backColorPicker.css('background-color', "f000");
			this.$colorPicker.css('backgroundColor', "#000");
		},

		setPropertyWindowTitle: function (title) {
			$("#element .el_title .text").text(title);
		},
		
		extract: function (options) {
			var context = this;
			$.each(options, function (key, value) {
				context.PARAMS[key] = value;
			});
		},

		setProperty: function (key, value) {
			this.PARAMS[key] = value;
		},

		closePropertyWindow: function (propertyType, x, y) {
			if (this.pane) {
				this.pane.hidden = true;
			}
		
		}
	};
	
});
