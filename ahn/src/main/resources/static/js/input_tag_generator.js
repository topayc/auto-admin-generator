class InputTagGenerator {
  constructor(
    tableName,
    columnMapList,
    mountContainer,
    innerContainer,
    type
  ) {
    this.tableName = tableName;
    this.columnMapList = columnMapList;
    this.mountContainer = mountContainer;
    this.innerContainer = innerContainer;
    //console.log("selector : " + innerContainer);
    this.type = type == null || type == "" ? "VIEW" : type;
    this.generateTag();
  }

  get() {
    return this.styleHtml;
  }

  getTodayMidnightString(type) {
    const now = new Date();
    const year = now.getFullYear();
    const month = (now.getMonth() + 1).toString().padStart(2, "0");
    const day = now.getDate().toString().padStart(2, "0");
    if (type === 0) {
      return `${year}-${month}-${day}T12:00`;
    } else if (type === 1) {
      return `${year}-${month}-${day}`;
    } else if (type === 3) {
    }
  }

  /**
   * type : VIEW, CREATE, UPDATE 중의 하나의 값을 가짐
   * 타입에 따라 각각의 태그를 생성
   * 기본적으로 VIEW 로 설정되며, 이 메서드를 통해 변경가능
   * 
   */
  setType(type){
    this.type = type;
  }

  setMountContainer(mountContainer){
    this.mountContainer = mountContainer;
  }

  setInnerContainer(innerConainer){
    this.innerContainer = innerConainer;
  }

  mount() {
    if (!this.mountContainer) {
      throw new Error(
        "생성할 dom 을 올릴 컨테이너 선택자가 지정되지 않았습니다. : " +
          this.mountContainer
      );
      console.error(
        "생성할 dom 을 올릴 컨테이너 선택자가 지정되지 않았습니다. : " +
          this.mountContainer
      );
      return;
    }

    const mountContainerElement = document.querySelector(this.mountContainer);

    if (!mountContainerElement) {
      throw new Error(
        "생성할 dom 을 올릴 컨테이너를 찾을 수 없습니다. : " +
          this.mountContainer
      );
      console.error(
        "생성할 dom 을 올릴 컨테이너를 찾을 수 없습니다. 컨테이너 ID 를 확인해주세요: " +
          this.mountContainer
      );
      return;
    }

    /** 마운트 컨테이너의 내용을 비워줌 , 이 작업을 빼놓으면 계속 쌓임 */
    mountContainerElement.innerHTML = "";

    if (this.innerContainer) {
      this.generatedDomList.forEach((dom) => {
        if (!(dom instanceof Node)){
          return;
        }
        var middleContainerDom = document.createElement("div");
        middleContainerDom.classList.add(
          ...this.innerContainer.split(" ")
        );
        middleContainerDom.setAttribute("style", "font-size : 0.8rem");
        middleContainerDom.appendChild(dom);
        mountContainerElement.append(middleContainerDom);
      });
    } else {
      this.generatedDomList.forEach((dom) => mountContainerElement.appendChild(dom));
    }
  }

  generateTag() {
    this.generatedDomList = this.columnMapList.map((columnMaps) => {
      let columnName = columnMaps["COLUMN_NAME"];
      let columnType = columnMaps["COLUMN_TYPE"];
      let columnKey = columnMaps["COLUMN_KEY"];
      let comment = columnMaps["COLUMN_COMMENT"];
      let extra = columnMaps["EXTRA"];

      const idx = columnType.indexOf("(");
      const baseType = idx > 0 ? columnType.substring(0, idx) : columnType;

      /** 정수 타입 생성*/
      if (
        baseType.toUpperCase() == "INT" ||
        baseType.toUpperCase() == "INTEGER" ||
        baseType.toUpperCase() == "BIGINT" ||
        baseType.toUpperCase() == "SMALLINT" ||
        baseType.toUpperCase() == "TINYINT" ||
        baseType.toUpperCase() == "MEDIUMINT"
      ) {
        let pkStr = "";
        if (columnKey != null && columnKey.toLowerCase().includes("pri")) {
          pkStr = "(PK)";
        }

        if (columnKey == "PRI") {
          /// form group div 생성
          const formGroup = document.createElement("div");
          formGroup.classList.add("form-group");

          // label 생성
          const label = document.createElement("label");
          label.setAttribute("for", `${this.tableName}_${columnName}`);
          label.setAttribute("style", "font-size : 0.8rem;");
          label.innerHTML = `${columnName} ${pkStr}`;

          const input = document.createElement("input");
          input.name = `${this.tableName}_${columnName}`;
          input.id = `${this.tableName}_${columnName}`;
          input.classList.add(
            ..."form-control text-sm form-control-sm".split(" ")
          );
          input.placeholder = "정수";
          input.type = "number";

          formGroup.appendChild(label);
          formGroup.appendChild(input);

          if(extra.toUpperCase().includes("AUTO") && this.type != "VIEW" ) {
            input.setAttribute("disabled", "disabled");
            input.placeholder = "";
          }
          return formGroup;

        } else {
          /// form group div 생성
          const formGroup = document.createElement("div");
          formGroup.classList.add("form-group");
          
          if (this.type == "update" || this.type == "create"){
            const label = document.createElement("label");
            label.setAttribute("style", "font-size : 0.8rem;");
            label.innerHTML = `${columnName}`;
            label.htmlFor = `${this.tableName}_${columnName}`;

            const input = document.createElement("input");
            input.type = "number";
            input.placeholder = "정수";
            input.name = `${this.tableName}_${columnName}`;
            input.id = `${this.tableName}_${columnName}`;
            input.classList.add(
              ..."form-control float-right text-sm form-control-sm".split(" ")
            );

            formGroup.appendChild(label);
            formGroup.appendChild(input);
            
          }else {
            const label = document.createElement("label");
            label.setAttribute("style", "font-size : 0.8rem;");
            label.innerHTML = `${columnName}`;
            //범위그룹 div
            const inputGroup = document.createElement("div");
            inputGroup.classList.add("input-group");

            const inputGroupPrepend = document.createElement("div");
            inputGroupPrepend.classList.add("input-group-prepend");

            //범위 검색을 위한 첫번째 input 생성
            const input1 = document.createElement("input");
            input1.type = "number";
            input1.name = `${this.tableName}_${columnName}_start`;
            input1.id = `${this.tableName}_${columnName}_start`;
            input1.classList.add(
              ..."form-control float-right text-sm form-control-sm".split(" ")
            );
            input1.placeholder = "정수";
            input1.type = "number";

            const dash = document.createTextNode("   -   ");

            const input2 = document.createElement("input");
            input2.type = "number";
            input2.name = `${this.tableName}_${columnName}_end`;
            input2.id = `${this.tableName}_${columnName}_end`;
            input2.classList.add(
              ..."form-control float-right text-sm form-control-sm".split(" ")
            );
            input2.placeholder = "정수";
            input2.type = "number";

            inputGroup.appendChild(inputGroupPrepend);
            inputGroup.appendChild(input1);
            inputGroup.appendChild(dash);
            inputGroup.appendChild(input2);

            formGroup.appendChild(label);
            formGroup.appendChild(inputGroup);
          }

          return formGroup;
        }
      }

      if (
        baseType.toUpperCase() == "VARCHAR" ||
        baseType.toUpperCase() == "CHAR" ||
        baseType.toUpperCase() == "TEXT" ||
        baseType.toUpperCase() == "SET"
      ) {
        let pkStr = "";
        if (columnKey != null && columnKey.toLowerCase().includes("pri")) {
          pkStr = "(PK)";
        }
        const formGroup = document.createElement("div");
        formGroup.classList.add("form-group");
        
        const label = document.createElement("label");
        label.setAttribute("style", "font-size : 0.8rem;");
        label.innerHTML = `${columnName}`;
        label.setAttribute("for", `${this.tableName}_${columnName}`);

        /* comment 에 file 관련 단어가 있을 경우에는 file upload tag 생성 */
        if (comment.toUpperCase().includes("FILE")){
          
          label.classList.add("form-label");
          label.innerHTML = `${columnName} `;
          
          const fileInput = document.createElement("input");
          fileInput.classList.add("form-control");
          fileInput.classList.add("form-control-sm");
         

          fileInput.type = "file";
          fileInput.id =`${this.tableName}_${columnName}`;
          fileInput.name = `${this.tableName}_${columnName}`;
          formGroup.appendChild(label);
          formGroup.appendChild(fileInput);
         
          return formGroup;
         
         }else {
          
          const input = document.createElement("input");
          input.name = `${this.tableName}_${columnName}`;
          input.id = `${this.tableName}_${columnName}`;
          input.classList.add(
            ..."form-control text-sm form-control-sm".split(" ")
          );
          input.placeholder = "문자";
          input.type = comment.toUpperCase().includes('PASS') || comment.toUpperCase().includes('PASSWORD') ? "password"  : "text";
          input.placeholder = comment.toUpperCase().includes('PASS') || comment.toUpperCase().includes('PASSWORD') ? "password"  : "";

  
          formGroup.appendChild(label);
          formGroup.appendChild(input);
          return formGroup;
         }
    
      }

      if (
        baseType.toUpperCase() == "DATETIME" ||
        baseType.toUpperCase() == "TIMESTAMP" ||
        baseType.toUpperCase() == "DATE" ||
        baseType.toUpperCase() == "DATE" ||
        baseType.toUpperCase() == "TIME"
      ) {
        /* 조회 페이지가 아닌 생성, 수정 페이지에서는 날짜 관련 태그는 필요 없음*/
        if (this.type != "VIEW"){
          return "";
        }
        let type = "";
        let timeText;
        if (
          baseType.toUpperCase() == "DATETIME" ||
          baseType.toUpperCase() == "TIMESTAMP"
        ) {
          type = "datetime-local";
          timeText = this.getTodayMidnightString(0);
          //console.log("formatting");
          //console.log(timeText);
        } else if (baseType.toUpperCase() == "DATE") {
          type = "date";
          timeText = this.getTodayMidnightString(1);
          //console.log("formatting");
          //console.log(timeText);
        } else if (baseType.toUpperCase() == "TIME") {
          type = "time";
          timeText = this.getTodayMidnightString(3);
          console.log("formatting");
          console.log(timeText);
        }
        
        /// form group div 생성
        const formGroup = document.createElement("div");
        formGroup.classList.add("form-group");

        // label 생성
        const label = document.createElement("label");
        label.setAttribute("style", "font-size : 0.8rem;");
        label.innerHTML = `${columnName}`;

        //범위그룹 div
        const inputGroup = document.createElement("div");
        inputGroup.classList.add("input-group");

        const inputGroupPrepend = document.createElement("div");
        inputGroupPrepend.classList.add("input-group-prepend");

        //범위 검색을 위한 첫번째 input 생성
        const input1 = document.createElement("input");
        input1.type = type;
        input1.name = `${this.tableName}_${columnName}_start`;
        input1.id = `${this.tableName}_${columnName}_start`;
        input1.classList.add(
          ..."form-control float-right text-sm form-control-sm".split(" ")
        );
        input1.value = timeText;

        const dash = document.createTextNode("   -   ");

        const input2 = document.createElement("input");
        input2.type = type;
        input2.name = `${this.tableName}_${columnName}_end`;
        input2.id = `${this.tableName}_${columnName}_end`;
        input2.classList.add(
          ..."form-control float-right text-sm form-control-sm".split(" ")
        );
        input2.value = timeText;

        inputGroup.appendChild(inputGroupPrepend);
        inputGroup.appendChild(input1);
        inputGroup.appendChild(dash);
        inputGroup.appendChild(input2);

        formGroup.appendChild(label);
        formGroup.appendChild(inputGroup);
        return formGroup;
      }

      if (baseType.toUpperCase() == "ENUM") {
        const start = columnType.indexOf("(");
        const end = columnType.lastIndexOf(")");
        if (start > 0 && end > start) {
          let optionArr = columnType
            .substring(start + 1, end)
            .replaceAll("'", "")
            .split(",");
          //console.log( "============" );
          //console.log(optionArr)
          let optionObj = {};
          optionArr.forEach((option) => (optionObj[option] = option));

          if (comment != null && comment != "" && comment != " ") {
            let commentOptionArr = comment.split(",");

            //console.log( "************" );
            //console.log(commentOptionArr);
            commentOptionArr.forEach((str) => {
              const key = str.split("=")[0].trim().replace(",", "");

              const value = str.split("=")[1].replace(",", "");
              //console.log(key  + "  : " + value);
              //console.log( "option : " );
              //console.log( optionObj);
              if (optionObj.hasOwnProperty(key)) {
                optionObj[key] = value;
              }
            });
          }

          //select 생성

          /// form group div 생성
          const formGroup = document.createElement("div");
          formGroup.classList.add("form-group");

          // label 생성
          const label = document.createElement("label");
          label.setAttribute("style", "font-size : 0.8rem;");
          label.innerHTML = `${columnName}`;

          const select = document.createElement("select");
          select.classList.add(
            ..."form-control text-sm form-control-sm".split(" ")
          );
          select.name = `${this.tableName}_${columnName}`;
          select.id = `${this.tableName}_${columnName}`;

          const optionDefault = document.createElement("option");
          optionDefault.value = "";
          optionDefault.innerHTML = `${columnName} 선택`;

          select.appendChild(optionDefault);

          Object.entries(optionObj).forEach(([key, value]) => {
            const op = document.createElement("option");
            op.value = key;
            op.innerHTML = value.replace("'", "");

            select.appendChild(op);
          });

          formGroup.appendChild(label);
          formGroup.appendChild(select);
          return formGroup;
        }
      }
    });
  }
}


