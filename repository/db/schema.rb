# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20180624223205) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "arguments", force: :cascade do |t|
    t.string "argument_name", null: false
    t.string "argument_type", null: false
    t.integer "i_method_id", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["i_method_id"], name: "index_arguments_on_i_method_id"
  end

  create_table "attributes", force: :cascade do |t|
    t.string "attribute_name", null: false
    t.string "attribute_type", null: false
    t.integer "i_class_id", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["i_class_id"], name: "index_attributes_on_i_class_id"
  end

  create_table "event_arguments", force: :cascade do |t|
    t.string "argument_type"
    t.string "value"
    t.bigint "event_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["event_id"], name: "index_event_arguments_on_event_id"
  end

  create_table "events", force: :cascade do |t|
    t.string "origin_class"
    t.string "destination_class"
    t.string "origin_hash"
    t.string "destination_hash"
    t.datetime "timestamp"
    t.string "kind"
    t.string "signature"
    t.string "source_location"
    t.string "class_instance"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.string "project_name"
    t.bigint "project_id"
    t.index ["project_id"], name: "index_events_on_project_id"
  end

  create_table "i_classes", force: :cascade do |t|
    t.string "class_name", null: false
    t.string "qualified_name", null: false
    t.integer "method_count", null: false
    t.integer "attribute_count", null: false
    t.integer "lines_of_code", null: false
    t.string "class_hash", null: false
    t.integer "package_id", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["package_id"], name: "index_i_classes_on_package_id"
  end

  create_table "i_methods", force: :cascade do |t|
    t.string "method_name", null: false
    t.integer "start_of_method", null: false
    t.integer "length_of_method", null: false
    t.integer "lines_of_code", null: false
    t.string "return_type", null: false
    t.integer "argument_count", null: false
    t.string "key", null: false
    t.integer "i_class_id", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["i_class_id"], name: "index_i_methods_on_i_class_id"
  end

  create_table "method_invocations", force: :cascade do |t|
    t.string "invocation", null: false
    t.integer "i_method_id", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["i_method_id"], name: "index_method_invocations_on_i_method_id"
  end

  create_table "packages", force: :cascade do |t|
    t.string "package_name", null: false
    t.integer "class_count", null: false
    t.boolean "has_subpackages", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.integer "project_id", null: false
    t.string "package_path"
    t.index ["project_id"], name: "index_packages_on_project_id"
  end

  create_table "projects", force: :cascade do |t|
    t.string "project_name", null: false
    t.integer "num_packages", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  add_foreign_key "event_arguments", "events"
  add_foreign_key "events", "projects"
end
